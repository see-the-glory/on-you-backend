package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stg.onyou.model.entity.User;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.UserCreateRequest;
import stg.onyou.model.network.response.UserClubResponse;
import stg.onyou.model.network.response.UserResponse;
import stg.onyou.model.network.response.UserUpdateRequest;
import stg.onyou.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Api(tags = {"User API Controller"})
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{clubId}")
    public Header<UserClubResponse> selectUserClubResponse(@PathVariable Long clubId,  HttpServletRequest httpServletRequest){

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        return userService.selectUserClubResponse(userId, clubId);

    }

    @GetMapping("")
    public Header<UserResponse> getUserInfo(HttpServletRequest httpServletRequest){
        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        return userService.selectUser(userId);
    }

    @PutMapping("")
    public Header<Object> updateUserInfo(@RequestBody UserUpdateRequest userUpdateRequest,
                                       HttpServletRequest httpServletRequest) {
        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        userService.updateUser(userUpdateRequest, userId);
        return Header.OK();
    }

    @PostMapping("")
    public Header<Object> registerUserInfo(@RequestBody UserCreateRequest userCreateRequest,
                                           HttpServletRequest httpServletRequest) {
        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        userService.registerUserInfo(userCreateRequest, userId);
        return Header.OK();
    }

}
