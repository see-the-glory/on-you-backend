package stg.onyou.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Block;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.AlarmType;
import stg.onyou.model.InterestCategory;
import stg.onyou.model.Role;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.FindPwRequest;
import stg.onyou.model.network.request.PushAlarmUpdateRequest;
import stg.onyou.model.network.request.UserCreateRequest;
import stg.onyou.model.network.response.*;
import stg.onyou.repository.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private UserClubRepository userClubRepository;
    @Autowired
    private UserClubScheduleRepository userClubScheduleRepository;
    @Autowired
    private UserClubQRepositoryImpl userClubQRepositoryImpl;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private FeedRepository feedRepository;
    @Autowired
    private UserBlockRepository userBlockRepository;
    @Autowired
    private SuggestionRepository suggestionRepository;
    @Autowired
    private AwsS3Service awsS3Service;
    @Autowired
    private FirebaseCloudMessageService firebaseCloudMessageService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private InterestRepository interestRepository;

    public Header<UserResponse> selectUser(Long id) {

        return userRepository.findById(id)
                .map(user -> Header.OK(selectUserResponse(user)))
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
    }

    private UserResponse selectUserResponse(User user) {
        List<InterestCategory> interests = user.getInterests().stream().map(Interest::getCategory).collect(Collectors.toList());
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .organizationName(Optional.ofNullable(user.getOrganization())
                        .map(Organization::getName)
                        .orElse(null)
                )
                .birthday(user.getBirthday())
                .sex(user.getSex())
                .email(user.getEmail())
                .created(user.getCreated())
                .thumbnail(user.getThumbnail())
                .phoneNumber(user.getPhoneNumber())
                .interests(interests)
                .build();

        return userResponse;

    }

    public Header<UserClubResponse> selectUserClubResponse(Long userId, Long clubId) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        Club club = clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        UserClub userClub = userClubRepository.findByUserAndClub(user, club)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND)
                );

        UserClubResponse userClubResponse = UserClubResponse.builder()
                .userId(userClub.getUser().getId())
                .clubId(userClub.getClub().getId())
                .applyStatus(userClub.getApplyStatus())
                .approveDate(userClub.getApproveDate())
                .applyDate(userClub.getApplyDate())
                .role(userClub.getRole())
                .build();

        return Header.OK(userClubResponse);
    }

    public Optional<User> findUser(Long id) {
        return userRepository.findById(id);
    }

    public void updateUser(MultipartFile thumbnailFile, UserUpdateRequest userUpdateRequest, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        user.setName(userUpdateRequest.getName());
        user.setBirthday(userUpdateRequest.getBirthday());
        user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        String url = awsS3Service.uploadFile(thumbnailFile);
        user.setThumbnail(url);
        user.setUpdated(LocalDateTime.now());
        userRepository.save(user);
    }

    public User registerUserInfo(UserCreateRequest userCreateRequest) {

        boolean existMember = userRepository.existsUserByEmail(userCreateRequest.getEmail());

        if (existMember) return null;

        User user = new User(userCreateRequest);
        Organization organization = organizationRepository.findByName(userCreateRequest.getOrganizationName());
        user.setOrganization(organization);
        user.setCreated(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        user.setThumbnail("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_110x110.jpg");
        user.setClubPushAlarm('Y');
        user.setUserPushAlarm('Y');
        userRepository.save(user);

        // 관심사 저장
//        List<InterestCategory> interestCategories = userCreateRequest.getInterests();
//        List<Interest> interests = new ArrayList<>();
//        for (InterestCategory category : interestCategories) {
//            Interest interest = new Interest();
//            interest.setUser(user);
//            interest.setCategory(category);
//            interests.add(interest);
//            interestRepository.save(interest);
//        }

//        user.setInterests(interests);
        return user;
    }

    public String getUserEmailByNameAndPhoneNumber(String username, String phoneNumber) {
        User user = userRepository.findByNameAndPhoneNumber(username, phoneNumber).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return user.getEmail();
    }

    public Long getUserId(HttpServletRequest httpServletRequest) {
        String email = httpServletRequest.getUserPrincipal().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return user.getId();
    }

    public void withdrawAccount(Long userId){

        List<UserClub> userClubList = userClubRepository.findByUserId(userId);

        userClubList.forEach(userClub -> {
            if (userClub.getRole().equals(Role.MASTER)) {
                UserClub newLeader = userClubQRepositoryImpl.findLatestManager(userClub.getClub().getId())
                        .orElse(userClubQRepositoryImpl.findLatestMember(userClub.getClub().getId())
                                .orElse(null));

                if (newLeader != null) {
                    newLeader.setRole(Role.MASTER);
                    userClubRepository.save(newLeader);
                }
            }
            userClubRepository.delete(userClub);
        });

        // user 삭제
        userRepository.deleteById(userId);
    }

    public String getMaskedEmail(String email) {
        String regex = "\\b(\\S+)+@(\\S+.\\S+)";
        Matcher matcher = Pattern.compile(regex).matcher(email);
        if (matcher.find()) {
            String id = matcher.group(1); // 마스킹 처리할 부분인 userId

            int length = id.length();
            if (length < 3) { // 세글자 미만인 경우 모두 마스킹
                char[] c = new char[length];
                Arrays.fill(c, '*');
                return email.replace(id, String.valueOf(c));
            } else if (length == 3) {  // * 세글자인 경우 뒤 두글자만 마스킹
                return email.replaceAll("\\b(\\S+)[^@][^@]+@[^@][^@](\\S+)", "$1**@**$2");
            } else { // userId의 길이를 기준으로 세글자 초과인 경우 뒤 세자리를 마스킹 처리
                return email.replaceAll("\\b(\\S+)[^@][^@][^@]+@[^@][^@](\\S+)", "$1***@**$2");
            }
        }
        return email;

    }

    public void getUserByFindPwRequest(FindPwRequest findPwRequest) {
        String email = findPwRequest.getEmail();
        String username = findPwRequest.getUsername();
        String phoneNumber = findPwRequest.getPhoneNumber();
        String birthday = findPwRequest.getBirthday();

        User findUser = userRepository.findByEmailAndNameAndPhoneNumberAndBirthday(email, username, phoneNumber, birthday).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        emailService.sendSimpleMessage(findUser);
    }

    public void changeUserPassword(User user, String password) {

        String encodedPassword = passwordEncoder.encode(password);
        if(user.getPassword().equals(encodedPassword)){
            throw new CustomException(ErrorCode.SAME_PASSWORD_AS_BEFORE);
        }
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public void blockUser(Long blockerId, Long blockeeId) {

        User blocker =  userRepository.findById(blockerId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
        User blockee =  userRepository.findById(blockeeId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );


        UserBlock userBlock = UserBlock.builder()
                .blocker(blocker)
                .blockee(blockee)
                .build();

        userBlockRepository.findByBlockerAndBlockee(blocker, blockee)
                .ifPresentOrElse(
                        existingUserBlock -> userBlockRepository.delete(existingUserBlock),
                        () -> userBlockRepository.save(userBlock)
                );
   }

    public void updateTargetToken(Long userId, String targetToken) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        user.setTargetToken(targetToken);
        userRepository.save(user);

    }

    public void setPushAlarm(Long userId, PushAlarmUpdateRequest pushAlarmUpdateRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        if(pushAlarmUpdateRequest.getAlarmType().equals(AlarmType.USER)) {
            user.setUserPushAlarm(pushAlarmUpdateRequest.getIsOnOff());
        } else {
            user.setClubPushAlarm(pushAlarmUpdateRequest.getIsOnOff());
        }

        userRepository.save(user);

    }

    public Header<List<BlockedUserResponse>> selectBlockUserList(Long userId) {

        User blocker = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        List<BlockedUserResponse> blockedUsers = userBlockRepository.findByBlocker(blocker)
                .stream()
                .map(UserBlock::getBlockee)
                .map(user -> new BlockedUserResponse(user.getId(), user.getName(), user.getThumbnail(), user.getOrganization().getName()))
                .collect(Collectors.toList());

        return Header.OK(blockedUsers);
    }

    public void saveSuggestion(Long userId, String content) {

        Suggestion suggestion = Suggestion.builder()
                .user(
                        userRepository.findById(userId)
                        .orElseThrow(
                                ()-> new CustomException(ErrorCode.USER_NOT_FOUND)
                        )
                )
                .content(content)
                .created(LocalDateTime.now())
                .build();

        suggestionRepository.save(suggestion);

    }

    public Header<DuplicateCheckResponse> duplicateEmailCheck(String email) {

        DuplicateCheckResponse duplicateCheckResponse = DuplicateCheckResponse.builder()
                .isDuplicated(userRepository.existsUserByEmail(email)==true?'Y':'N')
                .build();

        return Header.OK(duplicateCheckResponse);
    }

    public void checkValidEmail(String email) {
        emailService.sendValidCheckEmail(email);
    }
}