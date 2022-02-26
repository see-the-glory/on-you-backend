package stg.onyou.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stg.onyou.model.network.request.UserApiRequest;

@RestController
@RequestMapping("/api/login") //localhost:8080/api/login
public class LoginController {

    INT number;
    number= 2;

    @PostMapping("/kakao/register")  //WHEN USER CLICKS 'REGISTER' BUTTON ON THEIR APP, APP CALLS 'POST localhost:8080/api/login/kakao/register'
    public void register(@RequestBody UserApiRequest user) //user.name, user.email_account, user.id
    {
        requestKakaoServer(user)   //React-native + Spring
    }

}
//주석
