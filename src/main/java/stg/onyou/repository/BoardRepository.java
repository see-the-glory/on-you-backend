package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Board;
import stg.onyou.model.entity.Feed;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
