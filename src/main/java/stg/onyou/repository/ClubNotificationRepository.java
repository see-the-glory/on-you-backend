package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.ClubNotification;

public interface ClubNotificationRepository extends JpaRepository<ClubNotification, Long> {
}
