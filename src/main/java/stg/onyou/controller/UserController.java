package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stg.onyou.config.jwt.JwtTokenProvider;
import stg.onyou.model.entity.User;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.FeedCreateRequest;
import stg.onyou.model.network.request.UserCreateRequest;
import stg.onyou.model.network.request.UserFindIdRequest;
import stg.onyou.model.network.response.UserClubResponse;
import stg.onyou.model.network.response.UserResponse;
import stg.onyou.model.network.response.UserUpdateRequest;
import stg.onyou.repository.UserRepository;
import stg.onyou.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static stg.onyou.model.entity.QUser.user;

@Api(tags = {"User API Controller"})
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/{clubId}")
    public Header<UserClubResponse> selectUserClubResponse(@PathVariable Long clubId, HttpServletRequest httpServletRequest) {

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        return userService.selectUserClubResponse(userId, clubId);

    }

    @GetMapping("")
    public Header<UserResponse> getUserInfo(HttpServletRequest httpServletRequest) {
        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        return userService.selectUser(userId);
    }

    @PutMapping("")
    public Header<Object> updateUserInfo(@RequestPart(value = "file") MultipartFile thumbnailFile,
                                         @RequestPart(value = "userUpdateRequest") UserUpdateRequest userUpdateRequest,
                                         HttpServletRequest httpServletRequest) {
        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        userService.updateUser(thumbnailFile, userUpdateRequest, userId);
        return Header.OK();
    }

    @PostMapping("/signup")
    public Header<Object> registerUserInfo(@RequestBody UserCreateRequest userCreateRequest) {
        String result = userService.registerUserInfo(userCreateRequest);
        return Header.OK("회원가입 완료!");
    }

    @GetMapping("/findId")
    public Header<Object> findUserId(@RequestBody UserFindIdRequest userFindIdRequest) {
        String username = userFindIdRequest.getUsername();
        String phoneNumber = userFindIdRequest.getPhoneNumber();
        String email = userService.getUserEmailByNameAndPhoneNumber(username, phoneNumber);
        return Header.OK(email);
    }

    // 로그인
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> user) {
        User member = (User) userRepository.findByEmail(user.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-MAIL 입니다."));
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        return jwtTokenProvider.createToken(member.getUsername(), member.getRole());
    }

}
