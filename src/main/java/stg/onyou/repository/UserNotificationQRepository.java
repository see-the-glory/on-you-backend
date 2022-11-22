package stg.onyou.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import stg.onyou.model.network.response.UserNotificationResponse;
import java.util.List;

@Repository
public interface UserNotificationQRepository {

    List<UserNotificationResponse> findUserNotificationList(Long userId);
}
