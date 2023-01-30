package stg.onyou.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.InterestCategory;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.FindPwRequest;
import stg.onyou.model.network.request.UserCreateRequest;
import stg.onyou.model.network.response.UserClubResponse;
import stg.onyou.model.network.response.UserResponse;
import stg.onyou.model.network.response.UserUpdateRequest;
import stg.onyou.repository.*;

import javax.servlet.http.HttpServletRequest;
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
    private ClubRepository clubRepository;
    @Autowired
    private AwsS3Service awsS3Service;

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
                .organizationName(user.getOrganization().getName())
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
        user.setRole(Collections.singletonList("ROLE_USER"));
        user.setCreated(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));
        user.setThumbnail("http://k.kakaocdn.net/dn/dpk9l1/btqmGhA2lKL/Oz0wDuJn1YV2DIn92f6DVK/img_110x110.jpg");
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

    public User getUserByFindPwRequest(FindPwRequest findPwRequest) {
        String email = findPwRequest.getEmail();
        String username = findPwRequest.getUsername();
        String phoneNumber = findPwRequest.getPhoneNumber();
        String birthday = findPwRequest.getBirthday();
        User findUser = userRepository.findByEmailAndNameAndPhoneNumberAndBirthday(email, username, phoneNumber, birthday).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return findUser;
    }

    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}