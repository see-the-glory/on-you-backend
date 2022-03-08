package stg.onyou.service;

import org.springframework.stereotype.Service;
import stg.onyou.model.entity.Feed;
import stg.onyou.repository.FeedRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FeedService {

    private final FeedRepository feedRepository;

    public FeedService(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    /**
     * feed upload
     */

    public Long upload(Feed feed) {
        feedRepository.save(feed);
        return feed.getId();
    }


    /**
     * 전체 feed 조회
     */
    public List<Feed> findFeeds() {
        return feedRepository.findAll();
    }

//    public Optional<Feed> findOne(Long feedId) {
//        return feedRepository.findById(feedId);
//    }

}