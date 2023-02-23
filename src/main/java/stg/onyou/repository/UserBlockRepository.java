package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.User;
import stg.onyou.model.entity.UserBlock;

import java.util.List;
import java.util.Optional;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {
    Optional<UserBlock> findByBlockerAndBlockee(User blocker, User blockee);

    List<UserBlock> findByBlocker(User blocker);
}
