package stg.onyou.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.MailDto;
import stg.onyou.service.EmailService;

@Controller
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("api/mail/findPw")
    public Header<String> sendMail(@RequestBody MailDto mailDto) {
        emailService.sendSimpleMessage(mailDto);
        return Header.OK("임시 비밀번호 발급 완료");
    }

}
