package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.FeedLikes;

import java.util.Optional;

public interface FeedLikesRepository extends JpaRepository<FeedLikes, Long>{
    Optional<FeedLikes> findLikesByUserIdAndFeedId(Long userId, Long feedId);
}
