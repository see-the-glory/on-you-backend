package stg.onyou.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.enums.AccessModifier;
import stg.onyou.model.enums.ActionType;
import stg.onyou.model.enums.PreferType;
import stg.onyou.model.enums.Role;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.MessageMetaData;
import stg.onyou.model.network.request.CommentCreateRequest;
import stg.onyou.model.network.request.FeedCreateRequest;
import stg.onyou.model.network.request.FeedSearch;
import stg.onyou.model.network.request.FeedUpdateRequest;
import stg.onyou.model.network.response.*;
import stg.onyou.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    private final ActionRepository actionRepository;
    @Autowired
    private final ClubNotificationRepository clubNotificationRepository;
    @Autowired
    private final UserNotificationRepository userNotificationRepository;
    @Autowired
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    @Autowired
    private final UserPreferenceRepository userPreferenceRepository;
    @Autowired
    private final FeedHashtagRepository feedHashtagRepository;
    @Autowired
    private final HashtagRepository hashtagRepository;
    @Autowired
    private final CommentLikesRepository commentLikesRepository;
    private final FirebaseMessaging fcm;

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
                .hasData(findFeedList.getTotalElements()!=0?true:false)
                .responses(findFeedList)
                .build();

        return response;
    }

    public FeedPageResponse selectFeedListByClub(Pageable page, String cursor, Long userId, Long clubId) {

        Page<FeedResponse> findFeedList = feedQRepositoryImpl.findFeedListByClub(page, cursor, userId, clubId);

        FeedPageResponse response = FeedPageResponse.builder()
                .hasData(findFeedList.getTotalElements()!=0?true:false)
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

        if( feed.getUser().getId().equals(userId) || isManagerOfClub(user, feed.getClub())){ // 삭제자가 feed의 생성자거나 Club의 매니저이상인 경우만 삭제 가능
            feed.setDelYn('y');
            feed.getComments().forEach(
                    comment -> {
                        comment.setDelYn('y');
                        commentRepository.save(comment);
                    }
            );
        } else {
            throw new CustomException(ErrorCode.NO_AUTH_DELETE_FEED);
        }

        feedRepository.save(feed);

    }

    private boolean isManagerOfClub(User user, Club club){

        UserClub userClub = userClubRepository.findByUserAndClub(user, club).orElseThrow(() -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND));
        return userClub.getRole().equals(Role.MANAGER) || userClub.getRole().equals(Role.MASTER) ;

    }

    @Transactional
    public Feed createFeed(FeedCreateRequest request, Long userId, List<FeedImage> feedImages) {

         Club club = clubRepository.findById(request.getClubId()).orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
         User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
         LocalDateTime now = LocalDateTime.now();

         //Feed Builder
         Feed feed = Feed.builder()
                .content(request.getContent())
                .delYn('n')
                .access(AccessModifier.PUBLIC)
                .created(now)
                .updated(now)
                .club(club)
                .user(user)
                .reportCount(0)
                .feedImages(feedImages)
                .build();

         //Action Builder : FEED_CREATE에 대한 Action 저장
         Action action = Action.builder()
                 .actionFeed(feed)
                 .actionClub(club)
                 .actionType(ActionType.FEED_CREATE)
                 .actioner(user)
                 .isProcessDone(false)
                 .created(now)
                 .build();

         //Feed를 생성한 것에 대해서 해당 클럽의 알림함에 넣어줘야 함. (FCM은채 안함)
        ClubNotification clubNotification = ClubNotification.builder()
                .club(club)
                .created(LocalDateTime.now())
                .action(action)
                .build();

        try {
            if( feed.getUser().getClubPushAlarm()=='Y' && feed.getUser().getTargetToken()!=null){

                MessageMetaData data = MessageMetaData.builder()
                        .type(ActionType.FEED_CREATE)
                        .actionId(action.getId())
                        .feedId(feed.getId())
                        .clubId(club.getId())
                        .build();

                Message fcmMessage = firebaseCloudMessageService.makeMessage(
                        feed.getUser().getTargetToken(),
                        "새로운 피드",
                        club.getName()+"에 새로운 피드가 올라왔습니다. ",
                        data);

                fcm.send(fcmMessage);
            }
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
        feedRepository.save(feed);
        actionRepository.save(action);
        clubNotificationRepository.save(clubNotification);

        // mention이 있을 경우 mention된 user에게 알림/푸시
        request.getMentionUserList().stream()
                .map( uid -> userRepository.findById(uid).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)))
                .forEach( mentionedUser -> notifyMentionUser(mentionedUser, user, feed, null));

        return feed;
    }

    private void notifyMentionUser(User mentionedUser, User mentioner, Feed feed, Comment comment) {
        //Action Builder : FEED_CREATE에 대한 Action 저장
        Action action = Action.builder()
                .actionFeed(feed)
                .actionType(ActionType.MENTION_USER)
                .actioner(mentioner)
                .isProcessDone(false)
                .created(LocalDateTime.now())
                .build();


        UserNotification userNotification = UserNotification.builder()
                .recipient(mentionedUser)
                .created(LocalDateTime.now())
                .action(action)
                .build();

        try {
            if( mentionedUser.getClubPushAlarm()=='Y' && mentionedUser.getTargetToken()!=null){

                MessageMetaData data = MessageMetaData.builder()
                        .type(ActionType.MENTION_USER)
                        .actionId(action.getId())
                        .feedId(Optional.ofNullable(feed).map(Feed::getId).orElse(null))
                        .commentId(Optional.ofNullable(comment).map(Comment::getId).orElse(null))
                        .build();

                Message fcmMessage = firebaseCloudMessageService.makeMessage(
                        feed.getUser().getTargetToken(),
                        "새로운 맨션",
                        mentioner.getName()+"님이 회원 님을 맨션했습니다.",
                        data);

                fcm.send(fcmMessage);
            }
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }

        actionRepository.save(action);
        userNotificationRepository.save(userNotification);
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
    public void createComment(Long userId, Long feedId, CommentCreateRequest commentCreateRequest) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment parentComment = Optional.ofNullable(commentCreateRequest.getParentId())
                .flatMap( parentId -> commentRepository.findById(parentId))
                .orElseGet(() -> null);

        Comment comment = Comment.builder()
                    .feed(feed)
                    .user(user)
                    .content(commentCreateRequest.getContent())
                    .created(LocalDateTime.now())
                    .updated(LocalDateTime.now())
                    .delYn('n')
                    .parent(Optional.ofNullable(parentComment).orElse(null))
                    .build();

        // parent가 있을 경우 Parent comment를 작성한 사람에 대해서 prefer이 있는 것으로 간주,
        // 그리고 대댓글 단 대상자에게 push, noti 주기
        if(parentComment != null){
            UserPreference userPreference = UserPreference.builder()
                    .user(user)
                    .preferType(PreferType.COMMENT_REPLY)
                    .preferUser(parentComment.getUser())
                    .created(LocalDateTime.now())
                    .build();

            userPreferenceRepository.save(userPreference);

            Action action = Action.builder()
                    .actionFeed(feed)
                    .actionType(ActionType.COMMENT_REPLY)
                    .actioner(user)
                    .isProcessDone(false)
                    .created(LocalDateTime.now())
                    .build();

            actionRepository.save(action);

            User recipient = parentComment.getUser();
            UserNotification userNotification = UserNotification.builder()
                    .action(action)
                    .recipient(recipient)
                    .created(LocalDateTime.now())
                    .build();

            userNotificationRepository.save(userNotification);

            try {
                if( recipient.getUserPushAlarm()=='Y' && recipient.getTargetToken()!=null
                        && !recipient.equals(user)){

                    MessageMetaData data = MessageMetaData.builder()
                            .type(ActionType.COMMENT_REPLY)
                            .actionId(action.getId())
                            .feedId(feed.getId())
                            .commentId(comment.getId())
                            .build();


                    Message fcmMessage = firebaseCloudMessageService.makeMessage(
                            recipient.getTargetToken(),
                            "새로운 답글",
                            user.getName()+"님의 답글이 달렸습니다.",
                            data);

                    fcm.send(fcmMessage);
                }
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        }

        //Action Builder : FEED_COMMENT에 대한 Action 저장
        Action action = Action.builder()
                .actionFeed(feed)
                .actionType(ActionType.FEED_COMMENT)
                .actioner(user)
                .isProcessDone(false)
                .created(LocalDateTime.now())
                .build();

        UserNotification userNotification = UserNotification.builder()
                .action(action)
                .recipient(feed.getUser())
                .created(LocalDateTime.now())
                .build();

        UserPreference userPreference = UserPreference.builder()
                .user(user)
                .preferType(PreferType.FEED_COMMENT)
                .preferUser(feed.getUser())
                .created(LocalDateTime.now())
                .build();

        try {
            if( feed.getUser().getUserPushAlarm()=='Y' && feed.getUser().getTargetToken()!=null
                && !feed.getUser().equals(user)){

                MessageMetaData data = MessageMetaData.builder()
                        .type(ActionType.FEED_COMMENT)
                        .actionId(action.getId())
                        .feedId(feed.getId())
                        .commentId(comment.getId())
                        .build();

                Message fcmMessage = firebaseCloudMessageService.makeMessage(
                        feed.getUser().getTargetToken(),
                        "새로운 댓글",
                        user.getName()+"님의 댓글이 달렸습니다.",
                        data);

                fcm.send(fcmMessage);
            }
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }

        commentRepository.save(comment);
        actionRepository.save(action);
        userNotificationRepository.save(userNotification);
        userPreferenceRepository.save(userPreference);


        // mention이 있을 경우 mention된 user에게 알림/푸시
        commentCreateRequest.getMentionUserList().stream()
                .map( uid -> userRepository.findById(uid).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)))
                .forEach( mentionedUser -> notifyMentionUser(mentionedUser, user, null, comment));

    }

    public List<CommentResponse> getComments(Long feedId, Long userId) {

        List<Comment> comments = commentRepository.findByParentIdIsNullAndFeedIdOrderByCreatedDesc(feedId).stream()
                .filter(comment -> comment.getDelYn() == 'n')
                .collect(Collectors.toList());

        List<CommentResponse> commentResponses = new ArrayList<>();

        for (Comment comment : comments) {
            CommentResponse commentResponse =  new CommentResponse();
            commentResponse.setUserId(comment.getUser().getId());
            commentResponse.setCommentId(comment.getId());
            commentResponse.setThumbnail(comment.getUser().getThumbnail());
            commentResponse.setUserName(comment.getUser().getName());
            commentResponse.setContent(comment.getContent());
            commentResponse.setCreated(comment.getCreated());
            commentResponse.setLikeCount(
                    comment.getLikes().size()
            );
            commentResponse.setLikeYn(isLikes(userId, comment.getId()));

            List<LikeUserResponse> likeUserResponseList =
                    comment.getLikes().stream()
                            .map(like -> new LikeUserResponse(like.getUser().getThumbnail(), like.getUser().getName(), like.getCreated()))
                            .collect(Collectors.toList());

            commentResponse.setLikeUserResponseList(likeUserResponseList);

            List<CommentResponse> replyResponses = new ArrayList<>();

            List<Comment> replies = new ArrayList<>();
            if(comment.getParent()==null){
                replies = commentRepository.findByParentIdOrderByCreatedDesc(comment.getId())
                        .stream()
                        .filter(reply -> reply.getDelYn() == 'n')
                        .collect(Collectors.toList());
            }

            for (Comment reply : replies) {

                CommentResponse replyResponse = CommentResponse.builder()
                        .userId(reply.getUser().getId())
                        .commentId(reply.getId())
                        .thumbnail(reply.getUser().getThumbnail())
                        .userName(reply.getUser().getName())
                        .content(reply.getContent())
                        .likeCount(reply.getLikes().size())
                        .likeYn((isLikes(userId, reply.getId())))
                        .likeUserResponseList(
                                reply.getLikes().stream()
                                        .map(like -> new LikeUserResponse(like.getUser().getThumbnail(), like.getUser().getName(), like.getCreated()))
                                        .collect(Collectors.toList())
                        )
                        .created(reply.getCreated())
                        .build();

                replyResponses.add(replyResponse);
            }
            commentResponse.setReplies(replyResponses);
            commentResponses.add(commentResponse);
        }
        return commentResponses;
    }

    public int getLikesCount(Long feedId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
        return feed.getLikes().size();
    }

    public List<String> getHashtags(Feed feed) {
        List<String> result = new ArrayList<>();
        List<FeedHashtag> feedHashtags = feed.getFeedHashtags();
        for (FeedHashtag feedHashtag : feedHashtags) {
            result.add(feedHashtag.getHashtag().getHashtag());
        }
        return result;
    }

    private boolean isLikes(Long userId, Long commentId) {
        if(commentLikesRepository.findLikesByUserIdAndCommentId(userId, commentId).isPresent()){
            return true;
        } else {
            return false;
        }
    }

}