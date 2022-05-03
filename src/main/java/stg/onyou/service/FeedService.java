package stg.onyou.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.FeedSearch;
import stg.onyou.model.network.request.FeedUpdateRequest;
import stg.onyou.repository.ClubRepository;
import stg.onyou.repository.FeedRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final ClubRepository clubRepository;

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
        return feedRepository.findAllString(feedSearch);
    }

    /**
     * 특정 feed id -> feed 정보 값을 return
     */
    public Feed findById(Long id) {
        return feedRepository.findOne(id);
    }

    /**
     * 특정 feed 수정
     */
    @Transactional
    public void updateFeed(Long id, FeedUpdateRequest updateFeed) {
        Feed feed = feedRepository.findOne(id);
        feed.setContent(updateFeed.getContent());
        feed.setAccess(updateFeed.getAccess());
        feed.setUpdated(LocalDateTime.now());
    }

    /**
     * 특정 feed (id) 삭제
     */
    @Transactional
    public void deleteById(Long id) {
        Feed feed = feedRepository.findOne(id);
        feed.setDelYn('y');
    }


    /**
     * 특정 feed 신고. user는 feed에 대해 1번만 신고 가능.
     */
    @Transactional
    public void reportFeed(Long id) {
        Feed feed = feedRepository.findOne(id);
        feed.setReportCount(feed.getReportCount() + 1);
    }

}