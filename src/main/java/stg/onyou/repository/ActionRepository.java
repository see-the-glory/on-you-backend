package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}
