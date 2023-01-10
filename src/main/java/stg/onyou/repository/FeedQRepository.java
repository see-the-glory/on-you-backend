package stg.onyou.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import stg.onyou.model.network.request.ClubCondition;
import stg.onyou.model.network.response.ClubConditionResponse;
import stg.onyou.model.network.response.FeedResponse;

import java.util.List;

@Repository
public interface FeedQRepository {

    Page<FeedResponse> findFeedList(Pageable page, String customCursor, Long userId);
    Page<FeedResponse> findFeedListByClub(Pageable page, String customCursor, Long userId, Long clubId);

}
