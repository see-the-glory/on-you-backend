package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.FeedLikes;

public interface LikesRepository extends JpaRepository<FeedLikes, Long>{
    FeedLikes findLikesByUserIdAndFeedId(Long userId, Long feedId);
}
