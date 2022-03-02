package stg.onyou.service;

import org.springframework.stereotype.Service;
import stg.onyou.model.entity.Feed;
import stg.onyou.repository.FeedRepositoryImpl;

import java.util.List;

@Service
public class FeedService {
    private final FeedRepositoryImpl feedRepository;

    public FeedService(FeedRepositoryImpl feedRepository){
        this.feedRepository = feedRepository;
    }

    /**
    메인 페이지 feed 조회
     */

    public List<Feed> findFeedList(Long userId){
        return feedRepository.findFeedList(userId);
    }
}