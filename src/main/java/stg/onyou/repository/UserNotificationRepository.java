package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.UserNotification;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
}
