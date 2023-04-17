package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Action;
import stg.onyou.model.entity.User;
import stg.onyou.model.entity.UserAction;
import stg.onyou.model.entity.UserBlock;

import java.util.List;
import java.util.Optional;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    Optional<UserAction> findByUserAndAction(User user, Action action);

}
