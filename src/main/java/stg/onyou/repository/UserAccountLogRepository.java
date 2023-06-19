package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.UserAccountLog;

public interface UserAccountLogRepository extends JpaRepository<UserAccountLog, Long> {
}
