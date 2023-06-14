package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByParentIdIsNullAndFeedIdOrderByCreatedDesc(Long feedId);
    List<Comment> findByParentIdOrderByCreatedDesc(Long id);
    List<Comment> findByParentId(Long commentId);
}
