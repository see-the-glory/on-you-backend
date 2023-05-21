package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
import stg.onyou.model.network.request.*;
import stg.onyou.model.network.response.*;
import stg.onyou.repository.UserRepository;
import stg.onyou.repository.VersionRepository;
import stg.onyou.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
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
    private VersionRepository versionRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private final String REDIS_PREFIX = "jwt:";

    @GetMapping("/{clubId}")
    public Header<UserClubResponse> selectUserClubResponse(@PathVariable Long clubId, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return userService.selectUserClubResponse(userId, clubId);

    }

    @GetMapping("")
    public Header<UserResponse> getMyInfo(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return userService.selectUser(userId);
    }

    @GetMapping("/{userId}/profile")
    public Header<ProfileResponse> getUserProfile(@PathVariable Long userId,  HttpServletRequest httpServletRequest) {
        return userService.getUserProfile(userId);
    }

    @PutMapping("")
    public Header<Object> updateUserInfo(@RequestPart(value = "file") MultipartFile thumbnailFile,
                                         @RequestPart(value = "userUpdateRequest") UserUpdateRequest userUpdateRequest,
                                         HttpServletRequest httpServletRequest) throws Exception {
        Long userId = userService.getUserId(httpServletRequest);
        userService.updateUser(thumbnailFile, userUpdateRequest, userId);
        return Header.OK("User 정보 변경 완료");
    }

    @GetMapping("/myProfile")
    public Header<ProfileResponse> getMyProfile(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return userService.getMyProfile(userId);
    }

    @PutMapping("/myProfile")
    public Header<Object> updateMyProfile(@RequestPart(value = "thumbnail") MultipartFile thumbnailFile,
                                          @RequestPart(value = "backgroundImage") MultipartFile backGroundImageFile,
                                          @RequestBody @Valid UpdateMyProfileRequest updateMyProfileRequest,
                                         HttpServletRequest httpServletRequest) throws Exception {
        Long userId = userService.getUserId(httpServletRequest);
        userService.updateMyProfile(thumbnailFile, backGroundImageFile, updateMyProfileRequest, userId);
        return Header.OK("MyPage 정보 변경 완료");
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

        result.put("token", jwtTokenProvider.createToken(user.getUsername()));
        return new ResponseEntity<>(result, HttpStatus.OK);

    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) {
        User member = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }
        Map<String, Object> result = new HashMap<>();

        String jwtToken = jwtTokenProvider.createToken(member.getUsername());
        String redisKey = REDIS_PREFIX + member.getEmail();
        redisTemplate.opsForValue().set(redisKey, jwtToken);

        member.setLastLoginDate(LocalDateTime.now());
        userRepository.save(member);

        result.put("token", jwtToken);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // 회원 탈퇴
    @PostMapping("/withdraw")
    public Header<String> withdrawAccount(HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        userService.withdrawAccount(userId);
        return Header.OK("회원 탈퇴 완료");
    }

    @PostMapping("/changePassword")
    public Header<Object> changePassword(HttpServletRequest httpServletRequest,
                                         @RequestBody Map<String, String> password) {
        String email = httpServletRequest.getUserPrincipal().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        userService.changeUserPassword(user, password.get("password"));
        return Header.OK("비밀번호 변경 완료");
    }

    @PostMapping("/block")
    public Header<Object> blockUser(HttpServletRequest httpServletRequest,
                                         @Valid @RequestBody BlockUserRequest blockUserRequest) {

        Long blockerId = userService.getUserId(httpServletRequest);
        Long blockeeId = blockUserRequest.getUserId();

        userService.blockUser(blockerId, blockeeId);
        return Header.OK("차단/차단해제 완료");
    }

    @GetMapping("/blockUserList")
    public Header<List<BlockedUserResponse>> selectBlockUserList(HttpServletRequest httpServletRequest) {

        Long userId = userService.getUserId(httpServletRequest);

        return userService.selectBlockUserList(userId);

    }

    @PostMapping("/updateTargetToken")
    public Header<Object> updateTargetToken(HttpServletRequest httpServletRequest,
                                    @Valid @RequestBody TargetTokenRequest targetTokenRequest) {

        Long userId = userService.getUserId(httpServletRequest);
        String targetToken = targetTokenRequest.getTargetToken();

        userService.updateTargetToken(userId, targetToken);
        return Header.OK("targetToken이 전송되었습니다.");
    }

    @GetMapping("/pushAlarm")
    public Header<PushAlarmResponse> getPushAlarm(HttpServletRequest httpServletRequest) {

        Long userId = userService.getUserId(httpServletRequest);
        return Header.OK(userService.getPushAlarm(userId));
    }
    @PutMapping("/pushAlarm")
    public Header<Object> setPushAlarm(HttpServletRequest httpServletRequest,
                                       @RequestBody PushAlarmUpdateRequest pushAlarmUpdateRequest) {

        Long userId = userService.getUserId(httpServletRequest);
        userService.setPushAlarm(userId, pushAlarmUpdateRequest);
        return Header.OK("푸시알람 설정이 변경되었습니다.");
    }

    @PostMapping("/suggestion")
    public Header<String> saveSuggetsion(HttpServletRequest httpServletRequest,
                                       @Valid @RequestBody SuggestionRequest suggestionRequest) {

        Long userId = userService.getUserId(httpServletRequest);
        userService.saveSuggestion(userId, suggestionRequest.getContent());
        return Header.OK("건의사항이 등록되었습니다.");
    }

    @PostMapping("/duplicateEmailCheck")
    public Header<DuplicateCheckResponse> duplicateEmailCheck(HttpServletRequest httpServletRequest,
                                                              @Valid @RequestBody DuplicateEmailCheckRequest duplicateEmailCheck) {

        return userService.duplicateEmailCheck(duplicateEmailCheck.getEmail());
    }

    @PostMapping("/metaInfo")
    public Header<VersionCheckResponse> metaInfoRequest(HttpServletRequest httpServletRequest, @Valid @RequestBody MetaInfoRequest metaInfoRequest){
        Long userId = userService.getUserId(httpServletRequest);
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.setAppVersion(metaInfoRequest.getCurrentVersion());
        user.setDeviceInfo(metaInfoRequest.getDeviceInfo());

        String latestVersion = versionRepository.findById(1L).get().getLatestVersion();

        VersionCheckResponse versionCheckResponse= new VersionCheckResponse();
        if( isAppVersionLessThanLatest(metaInfoRequest.getCurrentVersion(), latestVersion) ){
            versionCheckResponse.setUpdateRequired('Y');
        } else {
            versionCheckResponse.setUpdateRequired('N');
        }
        versionCheckResponse.setLatestVersion(latestVersion);

        userRepository.save(user);
        return Header.OK(versionCheckResponse);
    }

    public boolean isAppVersionLessThanLatest(String appVersion, String latestAppVersion) {
        String[] appSegments = appVersion.split("\\.");
        String[] latestSegments = latestAppVersion.split("\\.");

        int appX = Integer.parseInt(appSegments[0]);
        int appY = Integer.parseInt(appSegments[1]);
        int latestX = Integer.parseInt(latestSegments[0]);
        int latestY = Integer.parseInt(latestSegments[1]);

        return appX < latestX || (appX == latestX && appY < latestY);
    }


}
