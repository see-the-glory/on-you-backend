package stg.onyou.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.ClubNotificationResponse;
import stg.onyou.model.network.response.UserNotificationResponse;
import stg.onyou.repository.ClubNotificationQRepositoryImpl;
import stg.onyou.repository.UserNotificationQRepositoryImpl;
import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private UserNotificationQRepositoryImpl userNotificationQRepositoryImpl;
    @Autowired
    private ClubNotificationQRepositoryImpl clubNotificationQRepositoryImpl;


    /**
     * 전체 카테고리 select1`
     */
    public Header<List<UserNotificationResponse>> selectUserNotification(Long userId){

        List<UserNotificationResponse> userNotificationList = userNotificationQRepositoryImpl.findUserNotificationList(userId);
        return Header.OK(userNotificationList);
    }

    public Header<List<ClubNotificationResponse>> selectClubNotification(Long clubId) {
        List<ClubNotificationResponse> clubNotificationList = clubNotificationQRepositoryImpl.findClubNotificationList(clubId);
        return Header.OK(clubNotificationList);
    }
}
