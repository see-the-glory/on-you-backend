package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.UserClubResponse;
import stg.onyou.service.UserService;

import javax.servlet.http.HttpServletRequest;

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
}
