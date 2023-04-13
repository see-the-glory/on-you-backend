package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.User;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.CheckEmailRequest;
import stg.onyou.model.network.request.CheckStringRequest;
import stg.onyou.model.network.request.FindPwRequest;
import stg.onyou.service.EmailService;
import stg.onyou.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = {"Email API Controller"})
@Slf4j
@RestController
public class EmailController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/api/mail/findPw")
    public Header<String> findPassword(@Valid @RequestBody FindPwRequest findPwRequest) {

        userService.getUserByFindPwRequest(findPwRequest);
            return Header.OK("임시 비밀번호 발급 완료");

    }

    @PostMapping("/api/mail/sendCheckEmail")
    public Header<String> checkValidEmail(@Valid @RequestBody CheckEmailRequest checkEmailRequest) {

        userService.checkValidEmail(checkEmailRequest.getEmail());
        return Header.OK("인증번호가 해당 메일로 발송되었습니다.");

    }

    @PostMapping("/api/mail/validCheck")
    public Header<String> validCheckString(@Valid @RequestBody CheckStringRequest checkStringRequest) {
        String redisKey = "check:" + checkStringRequest.getEmail();
        String savedCheckString = (String) redisTemplate.opsForValue().get(redisKey);

        if (savedCheckString == null) {
            throw new CustomException(ErrorCode.CHECK_STRING_NOT_FOUND);
        }

        // Redis 캐시에서 인증번호가 만료되지 않았는지 확인합니다.
        Long ttl = redisTemplate.getExpire(redisKey);
        if (ttl == null || ttl <= 0L) {
            throw new CustomException(ErrorCode.CHECK_STRING_EXPIRED);
        }

        if (!checkStringRequest.getCheckString().equals(savedCheckString)) {
            throw new CustomException(ErrorCode.CHECK_STRING_MISMATCH);
        }

        return Header.OK("이메일 인증이 확인되었습니다.");
    }


}
