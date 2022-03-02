package stg.onyou.repository;

import stg.onyou.model.entity.Feed;

import java.util.List;

public interface FeedRepository {

    List<Feed> findFeedList(Long userId);
}
