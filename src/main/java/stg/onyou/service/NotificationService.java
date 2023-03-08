package stg.onyou.service;

import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.ActionType;
import stg.onyou.model.Role;
import stg.onyou.model.entity.Action;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.User;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.ClubNotificationResponse;
import stg.onyou.model.network.response.UserNotificationResponse;
import stg.onyou.repository.ActionRepository;
import stg.onyou.repository.ClubNotificationQRepositoryImpl;
import stg.onyou.repository.UserNotificationQRepositoryImpl;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private UserNotificationQRepositoryImpl userNotificationQRepositoryImpl;
    @Autowired
    private ClubNotificationQRepositoryImpl clubNotificationQRepositoryImpl;
    @Autowired
    private ActionRepository actionRepository;


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

    public void readAction(Long actionId) {

        Action action = actionRepository.findById(actionId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.ACTION_NOT_FOUND)
                );

        action.setProcessDone(true);
        actionRepository.save(action);
    }
}
