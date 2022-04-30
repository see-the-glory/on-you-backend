package stg.onyou.service;

import org.springframework.stereotype.Service;
import stg.onyou.model.entity.Feed;
import stg.onyou.repository.FeedRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FeedService {

    private final FeedRepository feedRepository;

    public FeedService(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    /**
     * feed 등록
     */

    public void upload(Feed feed) {
        feedRepository.save(feed);
    }

    /**
     * 전체 public feed 조회
     */
    public List<Feed> findFeeds(String access) {
        return feedRepository.findAllByAccess(access);
    }


    /**
     * 검색 탭 클릭시, 특정 기준으로 public feed 조회
     * 무한스크롤을 위해 start index, windows size
     * ex. 1) 최신순
     */
    public List<Feed> findFeedsOrderByCreated(String access) {
        return feedRepository.findAllByAccessOrderByCreatedDesc(access);
    }


    /**
     * 특정그룹 내 전체 feed public feed 조회
     */
    // TODO ClubFeed join

    /**
     * 해시태그로 feed 검색
     */
    // TODO FeedHashtag join

    /**
     * input string 으로 hashtag list 검색
     * ㄱ -> 가정식
     */
    // TODO FeedHashtag join

    /**
     * title + content 로 feed 검색
     */
    public List<Feed> findAllByTitleOrContent(String title, String content) {
        return feedRepository.findAllByTitleContainingOrContentContaining(title, content);
    }

    /**
     * 특정 feed id -> feed 정보 값을 return
     */
    public Optional<Feed> findById(Long id) {
        return feedRepository.findById(id);
    }

    /**
     * 특정 feed 수정
     */
    public void updateFeed(Long id, Feed updateFeed) {
        Feed feed = feedRepository.getById(id);
        feed.setTitle(updateFeed.getTitle());
        feed.setContent(updateFeed.getContent());
        feed.setAccess(updateFeed.getAccess());
        feed.setUpdated(LocalDateTime.now());
    }

    /**
     * 특정 feed (id) 삭제
     */
    public void deleteById(Long id) {
        Feed feed = feedRepository.getById(id);
        feed.setDelYn('Y');
    }


    /**
     * 특정 feed 신고. user는 feed에 대해 1번만 신고 가능.
     */
//    public void reportFeed(int id) {
//        Feed feed = feedRepository.getById(id);
//        feed.setReportCount(feed.getReportCount() + 1);
//    }

}