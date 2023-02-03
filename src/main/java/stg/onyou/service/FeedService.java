package stg.onyou.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.AccessModifier;
import stg.onyou.model.Role;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.request.ClubCondition;
import stg.onyou.model.network.request.FeedCreateRequest;
import stg.onyou.model.network.request.FeedSearch;
import stg.onyou.model.network.request.FeedUpdateRequest;
import stg.onyou.model.network.response.*;
import stg.onyou.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages={"stg.onyou.repository"})
public class FeedService {

    @Autowired
    private final FeedRepository feedRepository;
    @Autowired
    private final FeedQRepositoryImpl feedQRepositoryImpl;
    @Autowired
    private final ClubRepository clubRepository;
    @Autowired
    private final FeedImageRepository feedImageRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserClubRepository userClubRepository;
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final FeedHashtagRepository feedHashtagRepository;
    @Autowired
    private final HashtagRepository hashtagRepository;

    /**
     * feed 등록
     */
    @Transactional
    public void upload(Feed feed) {
        feedRepository.save(feed);
    }

    /**
     * 전체 public feed 조회
     */
    public List<Feed> findFeeds() {
        return feedRepository.findAll();
    }


    public FeedPageResponse selectFeedList(Pageable page, String cursor, Long userId) {

        Page<FeedResponse> findFeedList = feedQRepositoryImpl.findFeedList(page, cursor, userId);

        FeedPageResponse response = FeedPageResponse.builder()
                .hasNext(hasNextElement(findFeedList, page, userId))
                .responses(findFeedList)
                .build();

        return response;
    }

    public FeedPageResponse selectFeedListByClub(Pageable page, String cursor, Long userId, Long clubId) {

        Page<FeedResponse> findFeedList = feedQRepositoryImpl.findFeedListByClub(page, cursor, userId, clubId);

        FeedPageResponse response = FeedPageResponse.builder()
                .hasNext(hasNextElement(findFeedList, page, userId))
                .responses(findFeedList)
                .build();

        return response;
    }

    private boolean hasNextElement(Page<FeedResponse> findFeedList, Pageable page, Long userId) {

        List<FeedResponse> feedResponseList = findFeedList.toList();
        if(feedResponseList.size()==0){
            return false;
        }

        FeedResponse lastElement = feedResponseList.get(feedResponseList.size()-1);

        Page<FeedResponse> hasNextList = feedQRepositoryImpl.findFeedList(page, lastElement.getCustomCursor(), userId);

        return hasNextList.getTotalElements() == 0 ? false : true;
    }


    /**
     * 특정그룹 내 전체 feed public feed 조회
     */
    public List<Feed> findAllByClub(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );
        return club.getFeeds();
    }

    /**
     * FeedSearch
     */
    public List<Feed> findAllByString(FeedSearch feedSearch) {
        return feedRepository.findAllString(feedSearch.getContent(), feedSearch.getHashtag());
    }

    /**
     * 특정 feed id -> feed 정보 값을 return
     */
    public Feed findById(Long id) {
        return feedRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
    }

    /**
     * 특정 feed 수정
     */
    @Transactional
    public void updateFeed(Long id, FeedUpdateRequest updateFeed) {
        Feed feed = feedRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
        feed.setContent(updateFeed.getContent());
        feed.setAccess(updateFeed.getAccess());
        feed.setUpdated(LocalDateTime.now());
        Club club = clubRepository.findById(updateFeed.getClubId()).orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
        feed.setClub(club);
    }

    /**
     * 특정 feed (id) 삭제
     */
    @Transactional
    public void deleteById(Long id, Long userId) {

        Feed feed = feedRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if( feed.getUser().getId().equals(id) || isManagerOfClub(user, feed.getClub())){ // 삭제자가 feed의 생성자r거나 Club의 매니저이상인 경우만 삭제 가능
            feed.setDelYn('y');
        } else {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        feedRepository.save(feed);

    }

    private boolean isManagerOfClub(User user, Club club){

        UserClub userClub = userClubRepository.findByUserAndClub(user, club).orElseThrow(() -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND));
        return !userClub.getRole().equals(Role.MEMBER);

    }

    @Transactional
    public Feed createFeed(FeedCreateRequest request, Long userId, List<FeedImage> feedImages) {
         Feed feed = Feed.builder()
                .content(request.getContent())
                .delYn('n')
                .access(AccessModifier.PUBLIC)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .club(clubRepository.findById(request.getClubId()).orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND)))
                .user(userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)))
                .reportCount(0)
                .feedImages(feedImages)
                .build();
         return feedRepository.save(feed);
    }

    @Transactional
    public void addFeedImage(Feed feed, String url) {
        FeedImage feedImage = FeedImage.builder()
                .feed(feed).url(url).build();

        feedImageRepository.save(feedImage);
    }

//    @Transactional
//    public void createLikeFeed(Long userId, Long feedId) {
//        Feed feed = feedRepository.findOne(feedId);
//        Optional<User> user = userRepository.findById(userId);
//
//        Likes like = Likes.builder()
//                .feed(feed)
//                .user(user.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)))
//                .build();
//        likesRepository.save(like);
//    }

    @Transactional
    public void commentFeed(Long userId, Long feedId, String content) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .feed(feed)
                .user(user)
                .content(content)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
    }

    public List<CommentResponse> getComments(Long id) {
        Feed feed = feedRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
        List<CommentResponse> resultList;
        if (feed == null) {
            throw new CustomException(ErrorCode.FEED_NOT_FOUND);
        } else {
             resultList = feed.getComments()
                     .stream()
                     .filter(comment -> comment.getDelYn() != 'y')
                     .sorted(Comparator.comparing(Comment::getCreated).reversed()).map(comment -> new CommentResponse(
                     comment.getUser().getId(), comment.getId(), comment.getUser().getThumbnail(), comment.getUser().getName(), comment.getContent(), comment.getCreated()
             )).collect(Collectors.toList());
        }
        return resultList;
    }

    public int getLikesCount(Long feedId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
        int count = 0;
        if (feed == null) {
            throw new CustomException(ErrorCode.FEED_NOT_FOUND);

        } else {
            List<FeedLikes> feedLikes = feed.getLikes();
            for (FeedLikes feedLike : feedLikes) {
                if (feedLike.isOnOff()) {
                    count += 1;
                }
            }
            return count;
        }
    }

    public List<String> getHashtags(Feed feed) {
        List<String> result = new ArrayList<>();
        List<FeedHashtag> feedHashtags = feed.getFeedHashtags();
        for (FeedHashtag feedHashtag : feedHashtags) {
            result.add(feedHashtag.getHashtag().getHashtag());
        }
        return result;
    }


}