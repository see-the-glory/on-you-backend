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
import stg.onyou.model.entity.EmailCheck;
import stg.onyou.model.entity.User;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.CheckEmailRequest;
import stg.onyou.model.network.request.CheckStringRequest;
import stg.onyou.model.network.request.FindPwRequest;
import stg.onyou.repository.EmailCheckRepository;
import stg.onyou.service.EmailService;
import stg.onyou.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;

@Api(tags = {"Email API Controller"})
@Slf4j
@RestController
public class EmailController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private EmailCheckRepository emailCheckRepository;

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
//        String redisKey = "check:" + checkStringRequest.getEmail();
//        String savedCheckString = (String) redisTemplate.opsForValue().get(redisKey);

        EmailCheck emailCheck = emailCheckRepository.findByEmail(checkStringRequest.getEmail())
                .orElseThrow(
                        () -> new CustomException(ErrorCode.EMAIL_CHECK_NOT_FOUND)
                );

        if (emailCheck.getCheckString() == null) {
            throw new CustomException(ErrorCode.CHECK_STRING_NOT_FOUND);
        }

        LocalDateTime createdTime = emailCheck.getCreated();
        LocalDateTime currentTime = LocalDateTime.now();

        // 현재 시간과 생성 시간을 비교하여 3분 경과 여부 확인
        if (currentTime.isAfter(createdTime.plusMinutes(3))) {
            throw new CustomException(ErrorCode.CHECK_STRING_EXPIRED);
        }

        if (!checkStringRequest.getCheckString().equals(emailCheck.getCheckString())) {
            throw new CustomException(ErrorCode.CHECK_STRING_MISMATCH);
        }

        return Header.OK("이메일 인증이 확인되었습니다.");
    }


}
