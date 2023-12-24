package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.BoardLikes;
import stg.onyou.model.entity.FeedLikes;

import java.util.Optional;

public interface BoardLikesRepository extends JpaRepository<BoardLikes, Long>{
    Optional<BoardLikes> findLikesByUserIdAndBoardId(Long userId, Long boardId);
}
