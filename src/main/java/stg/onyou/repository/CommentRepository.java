package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
