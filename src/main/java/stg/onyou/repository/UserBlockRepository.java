package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.UserBlock;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
}
