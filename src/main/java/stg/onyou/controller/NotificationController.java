package stg.onyou.controller;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.CategoryResponse;
import stg.onyou.model.network.response.UserNotificationResponse;
import stg.onyou.service.CategoryService;
import stg.onyou.service.NotificationService;
import stg.onyou.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = {"Notification API Controller"})
@Slf4j
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public Header<List<UserNotificationResponse>> selectAllCategories(HttpServletRequest httpServletRequest){

        Long userId = userService.getUserId(httpServletRequest);
        return notificationService.selectUserNotification(userId);
    }

}
