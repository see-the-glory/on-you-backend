package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Likes;

public interface LikesRepository extends JpaRepository<Likes, Long>{
    Likes findLikesByFeedIdAndUserId(Long feedId, Long userId);
}
