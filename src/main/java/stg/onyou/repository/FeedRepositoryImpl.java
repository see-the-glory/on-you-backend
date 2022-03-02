package stg.onyou.repository;

import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.Feed;

import java.util.List;

@Repository
public class FeedRepositoryImpl implements FeedRepository{


    @Override
    public List<Feed> findFeedList(Long userId) {
        return null;
    }
}
