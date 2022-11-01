package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import stg.onyou.model.entity.User;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.FindPwRequest;
import stg.onyou.service.EmailService;
import stg.onyou.service.UserService;

import javax.servlet.http.HttpServletRequest;

@Api(tags = {"Email API Controller"})
@Slf4j
@RestController
public class EmailController {

    private final EmailService emailService;
    private final UserService userService;

    public EmailController(EmailService emailService, UserService userService) {
        this.emailService = emailService;
        this.userService = userService;
    }

    @PostMapping("/api/mail/findPw")
    public Header<String> sendMail(@RequestBody FindPwRequest findPwRequest) {
        User findUser = userService.getUserByFindPwRequest(findPwRequest);
        if (findUser == null) {
            return Header.OK("일치하는 유저가 없습니다.");
        }
        else {
            emailService.sendSimpleMessage(findUser);
            return Header.OK("임시 비밀번호 발급 완료");
        }

    }

}
