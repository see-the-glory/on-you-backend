package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stg.onyou.service.UserService;

@Api(tags = {"User API Controller"})
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/oauth")
    public void getAuthCode(@RequestParam("code") String code){
        System.out.println("---");
        System.out.println(code);
        System.out.println("---");
    }
   /* @ResponseBody
    @GetMapping("/kakao")
    public Header<User> kakaoCallback(@RequestParam String code) throws CustomException {
        String token = userApiService.getKaKaoAccessToken(code);
        JsonElement kakaoUserInfo = userApiService.getKakaoUser(token);
        User user = userApiService.join(kakaoUserInfo);
        return Header.OK(user);
    }*/
//
//    @ResponseBody
//    @GetMapping("/kakao")
//    public Header<User> kakaoCallback(@RequestParam String token) throws CustomException {
//        JsonElement kakaoUserInfo = userApiService.getKakaoUser(token);
//        User user = userApiService.join(kakaoUserInfo);
//        return Header.OK(user);
//    }
}
