package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.CommentLikes;
import stg.onyou.model.entity.FeedLikes;

import java.util.Optional;

public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long>{
    Optional<CommentLikes> findLikesByUserIdAndCommentId(Long userId, Long commentId);
}