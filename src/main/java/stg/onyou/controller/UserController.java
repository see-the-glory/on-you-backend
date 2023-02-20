package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stg.onyou.config.jwt.JwtTokenProvider;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.User;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.BlockUserRequest;
import stg.onyou.model.network.request.UserCreateRequest;
import stg.onyou.model.network.request.UserFindIdRequest;
import stg.onyou.model.network.response.UserClubResponse;
import stg.onyou.model.network.response.UserResponse;
import stg.onyou.model.network.response.UserUpdateRequest;
import stg.onyou.repository.UserRepository;
import stg.onyou.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
        Long userId = userService.getUserId(httpServletRequest);
        return userService.selectUserClubResponse(userId, clubId);

    }

    @GetMapping("")
    public Header<UserResponse> getUserInfo(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return userService.selectUser(userId);
    }

    @PutMapping("")
    public Header<Object> updateUserInfo(@RequestPart(value = "file") MultipartFile thumbnailFile,
                                         @RequestPart(value = "userUpdateRequest") UserUpdateRequest userUpdateRequest,
                                         HttpServletRequest httpServletRequest) throws Exception {
        Long userId = userService.getUserId(httpServletRequest);
        userService.updateUser(thumbnailFile, userUpdateRequest, userId);
        return Header.OK("User 정보 변경 완료");
    }

    @PostMapping("/findId")
    public Header<Object> findUserId(@RequestBody UserFindIdRequest userFindIdRequest) {
        String username = userFindIdRequest.getUsername();
        String phoneNumber = userFindIdRequest.getPhoneNumber();
        String email = userService.getUserEmailByNameAndPhoneNumber(username, phoneNumber);
        String maskedEmail = userService.getMaskedEmail(email);
        return Header.OK(maskedEmail);
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUserInfo(@RequestBody UserCreateRequest userCreateRequest) {
        User user  = userService.registerUserInfo(userCreateRequest);
        Map<String, Object> result = new HashMap<>();
        if (user == null) {
            throw new CustomException(ErrorCode.USER_APPROVE_ERROR);
        }

        result.put("token", jwtTokenProvider.createToken(user.getUsername(), user.getRole()));
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, String> user) {
        User member = userRepository.findByEmail(user.get("email"))
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAIL));
        if (!passwordEncoder.matches(user.get("password"), member.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("token", jwtTokenProvider.createToken(member.getUsername(), member.getRole()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 회원 탈퇴
    @PostMapping("/withdraw")
    public Header<String> withdrawAccount(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        userService.withdrawAccount(userId);
        return Header.OK("회원 탈퇴 완료");
    }

    @PostMapping("/changePw")
    public Header<Object> changePassword(HttpServletRequest httpServletRequest,
                                         @RequestBody Map<String, String> password) {
        String email = httpServletRequest.getUserPrincipal().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        userService.changeUserPassword(user, password.get("password"));
        return Header.OK("비밀번호 변경 완료");
    }

    @PostMapping("/block")
    public Header<Object> blockUser(HttpServletRequest httpServletRequest,
                                         @RequestBody BlockUserRequest blockUserRequest) {

        Long blockerId = userService.getUserId(httpServletRequest);
        Long blockeeId = blockUserRequest.getUserId();

        userService.blockUser(blockerId, blockeeId);
        return Header.OK("해당 사용자가 차단되었습다.");
    }
}
