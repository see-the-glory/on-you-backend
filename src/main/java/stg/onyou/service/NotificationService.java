//package stg.onyou.service;
//
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import stg.onyou.exception.CustomException;
//import stg.onyou.exception.ErrorCode;
//import stg.onyou.model.entity.UserNotification;
//import stg.onyou.model.network.Header;
//import stg.onyou.model.network.response.UserNotificationResponse;
//import stg.onyou.repository.UserNotificationQRepository;
//import stg.onyou.repository.UserNotificationRepository;
//
//import javax.transaction.Transactional;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class NotificationService {
//
//    @Autowired
//    private UserNotificationRepository userNotificationRepository;
//
//    @Autowired
//    private UserNotificationQRepository userNotificationQRepository;
//
//    @Autowired
//    private ModelMapper modelMapper;
//
//    /**
//     * 전체 카테고리 select
//     */
//    public Header<List<UserNotificationResponse>> selectUserNotification(Long userId){
//
//        List<UserNotificationResponse> userNotificationList = userNotificationQRepository.findUserNotificationList(userId);
//        return Header.OK(userNotificationList);
//    }
//
//}
