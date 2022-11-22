package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.ClubLikes;
import stg.onyou.model.entity.User;
import stg.onyou.model.entity.UserNotification;

import java.util.List;
import java.util.Optional;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    List<UserNotification> findByRecipient(User user);
}
