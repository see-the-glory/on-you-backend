package stg.onyou.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.ActionType;
import stg.onyou.model.ApplyStatus;
import stg.onyou.model.RecruitStatus;
import stg.onyou.model.Role;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.*;
import stg.onyou.model.network.response.*;
import stg.onyou.repository.*;
import stg.onyou.repository.ClubNotificationRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ClubService {

    @Autowired
    private FirebaseCloudMessageService firebaseCloudMessageService;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private UserClubRepository userClubRepository;
    @Autowired
    private ClubScheduleRepository clubScheduleRepository;
    @Autowired
    private UserClubScheduleRepository userClubScheduleRepository;
    @Autowired
    private ClubLikesRepository clubLikesRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ClubQRepository clubQRepository;
    @Autowired
    private ClubCategoryRepository clubCategoryRepository;
    @Autowired
    private ActionRepository actionRepository;
    @Autowired
    private UserNotificationRepository userNotificationRepository;
    @Autowired
    private ClubNotificationRepository clubNotificationRepository;
    private final FirebaseMessaging fcm;

    public ClubService(FirebaseMessaging fcm) {
        this.fcm = fcm;
    }

    /**
     * 특정 클럽 select
     */
    public Header<ClubResponse> selectClub(Long id){

        return clubRepository.findById(id)
                .map(club -> Header.OK(selectClubResponse(club)))
                .orElseThrow(
                        ()-> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );
    }

    public ClubPageResponse selectClubList(Pageable page, ClubCondition clubCondition, String customCursor, Long userId) {

        Page<ClubConditionResponse> findClubList = clubQRepository.findClubSearchList(page, clubCondition, customCursor, userId);

        ClubPageResponse response = ClubPageResponse.builder()
                .hasNext(hasNextElement(findClubList, page, clubCondition, userId))
                .responses(findClubList)
                .build();

        return response;
    }

    private boolean hasNextElement(Page<ClubConditionResponse> findClubList, Pageable page, ClubCondition clubCondition, Long userId) {

        List<ClubConditionResponse> clubConditionResponseList = findClubList.toList();
        if(clubConditionResponseList.size()==0){
            return false;
        }
        ClubConditionResponse lastElement = clubConditionResponseList.get(clubConditionResponseList.size()-1);

        Page<ClubConditionResponse> hasNextList = clubQRepository.findClubSearchList(page, clubCondition, lastElement.getCustomCursor(), userId);


        return hasNextList.getTotalElements() == 0 ? false : true;
    }

    public Header<ClubRoleResponse> selectClubRole(Long clubId, Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
        Club club = clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        ClubRoleResponse clubRoleResponse;
        UserClub userClub = userClubRepository.findByUserAndClub(user, club)
                .orElse(null);
        if(userClub == null){   // userClub이 존재하지 않을 경우에는 role, applyStatus 를 null로 반환
            clubRoleResponse = ClubRoleResponse.builder()
                    .userId(user.getId())
                    .clubId(club.getId())
                    .build();
        } else {
            clubRoleResponse = ClubRoleResponse.builder()
                    .userId(user.getId())
                    .clubId(club.getId())
                    .role(userClub.getRole())
                    .applyStatus(userClub.getApplyStatus())
                    .build();
        }

        return Header.OK(clubRoleResponse);

    }

    /**
     * 클럽 create
     */
    public Header<ClubResponse> createClub(ClubCreateRequest clubCreateRequest, Long userId){

        log.debug("createClub Service 진입");
        // category1과 category2가 같은 것을 선택하는 것 방지
        if(clubCreateRequest.getCategory2Id()==clubCreateRequest.getCategory1Id()){
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }


        log.debug("category 같은 것 방지 통과!");

        Optional<Club> duplicateCheckClub = clubRepository.findByName(clubCreateRequest.getClubName());
        if(duplicateCheckClub.isPresent()){
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }


        log.debug("409 에러 통과!");

        // 1. club 저장 : clubLongDesc는 optional값이므로 null체크
        Club club = Club.builder()
                .name(clubCreateRequest.getClubName())
                .shortDesc(clubCreateRequest.getClubShortDesc())
                .longDesc(
                        Optional.ofNullable(clubCreateRequest.getClubLongDesc())
                            .orElse(null)
                )
                .delYn('N')
                .thumbnail(clubCreateRequest.getThumbnailUrl())
                .recruitStatus(RecruitStatus.OPEN)
                .maxNumber(clubCreateRequest.getClubMaxMember())
                .isApproveRequired(clubCreateRequest.getIsApproveRequired())
                .created(LocalDateTime.now())
                .thumbnail(clubCreateRequest.getThumbnailUrl())
                .organization(organizationRepository.findById(1L).get())
                .build();

        Club savedClub = clubRepository.save(club);


        log.debug("club 저장");

        // 2. user_club 저장
        UserClub userClub = UserClub.builder()      // creator도 club의 멤버(마스터)니깐 UserClub에 저장
                .user(userRepository.findById(userId)
                        .orElseThrow(
                                () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                        )
                )
                .club(club)
                .applyStatus(ApplyStatus.APPROVED)
                .approveDate(LocalDateTime.now())
                .role(Role.MASTER)
                .build();

        userClubRepository.save(userClub);


        log.debug("userClub 저장");

        // 3. club_category 저장
        ClubCategory clubCategory1 = ClubCategory.builder()
                .club(savedClub)
                .category(categoryRepository.findById(clubCreateRequest.getCategory1Id())
                        .orElseThrow(
                                () -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
                        )
                )
                .sortOrder(0)
                .created(LocalDateTime.now())
                .build();

        clubCategoryRepository.save(clubCategory1);


        log.debug("category1 저장");

        if(clubCreateRequest.getCategory2Id() != null){
            ClubCategory clubCategory2 = ClubCategory.builder()
                    .club(savedClub)
                    .category(categoryRepository.findById(clubCreateRequest.getCategory2Id())
                            .orElseThrow(
                                    () -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
                            )
                    )
                    .sortOrder(1)
                    .created(LocalDateTime.now())
                    .build();

            clubCategoryRepository.save(clubCategory2);
        }


        log.debug("category2 저장");
        // 4. 저장 성공한 club의 reponse 생성하여 return
        ClubResponse clubResponse = selectClubResponse(savedClub);
        return Header.OK(clubResponse);


    }

    public Header<ClubResponse> updateClub(ClubUpdateRequest clubUpdateRequest, Long clubId) {

        // 1. Club UPDATE
        Club club = clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        club.setName(
                Optional.ofNullable(clubUpdateRequest.getClubName())
                        .orElse(club.getName())
        );
        club.setIsApproveRequired(
                Optional.ofNullable(clubUpdateRequest.getIsApproveRequired())
                        .orElse(club.getIsApproveRequired())
        );
        club.setRecruitStatus(
                Optional.ofNullable(clubUpdateRequest.getRecruitStatus())
                        .orElse(club.getRecruitStatus())
        );
        club.setMaxNumber(
                Optional.ofNullable(clubUpdateRequest.getClubMaxMember())
                        .orElse(club.getMaxNumber())
        );
        club.setShortDesc(
                Optional.ofNullable(clubUpdateRequest.getClubShortDesc())
                        .orElse(club.getShortDesc())
        );
        club.setLongDesc(
                Optional.ofNullable(clubUpdateRequest.getClubLongDesc())
                        .orElse(club.getLongDesc())
        );
        club.setThumbnail(
                Optional.ofNullable(clubUpdateRequest.getThumbnailUrl())
                        .orElse(club.getThumbnail())
        );
        club.setContactPhone(
                Optional.ofNullable(clubUpdateRequest.getContactPhone())
                        .orElse(club.getContactPhone())
        );
        club.setOrganization(
                organizationRepository.findById(
                        Optional.ofNullable(clubUpdateRequest.getOrganizationId())
                                .orElse(club.getOrganization().getId()))
                        .orElseThrow(
                            () -> new CustomException(ErrorCode.ORGANIZATION_NOT_FOUND)
                         )
        );
        club.setUpdated(LocalDateTime.now());

        Club savedClub = clubRepository.save(club);

        // 2. ClubCategory UPDATE

        clubCategoryRepository.deleteByClubId(club);

        if ( clubUpdateRequest.getCategory1Id() != null && clubUpdateRequest.getCategory2Id() != null ) {  // category1,2 둘다 있는 경우

            ClubCategory clubCategory1 = ClubCategory.builder()
                    .category(categoryRepository.findById(clubUpdateRequest.getCategory1Id())
                                    .orElseThrow(
                                            () -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
                                    ))
                    .club(club)
                    .created(LocalDateTime.now())
                    .updated(LocalDateTime.now())
                    .sortOrder(0)
                    .build();

            ClubCategory clubCategory2 = ClubCategory.builder()
                    .category(categoryRepository.findById(clubUpdateRequest.getCategory2Id())
                            .orElseThrow(
                                    () -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
                            ))
                    .club(club)
                    .created(LocalDateTime.now())
                    .updated(LocalDateTime.now())
                    .sortOrder(1)
                    .build();

            clubCategoryRepository.save(clubCategory1);
            clubCategoryRepository.save(clubCategory2);

        } else if ( clubUpdateRequest.getCategory1Id() != null && clubUpdateRequest.getCategory2Id() == null ){ // category1만 있는 경우

            ClubCategory clubCategory1 = ClubCategory.builder()
                    .category(categoryRepository.findById(clubUpdateRequest.getCategory1Id())
                            .orElseThrow(
                                    () -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
                            ))
                    .club(club)
                    .created(LocalDateTime.now())
                    .updated(LocalDateTime.now())
                    .sortOrder(0)
                    .build();

            clubCategoryRepository.save(clubCategory1);
        }

        return Header.OK(selectClubResponse(savedClub));
    }

    public void deleteClub(Long clubId, Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        Club club  = clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        UserClub userClub  = userClubRepository.findByUserAndClub(user, club)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND)
                );

        if ( !userClub.getRole().equals(Role.MASTER) ){
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        if ( club.getRecruitNumber() != 1 ){
            throw new CustomException(ErrorCode.CLUB_DELETE_EXCEPTION);
        }

        clubRepository.deleteById(clubId);
    }

    /**
     * 클럽 가입요청 : 디폴트로 APPLIED 상태로 UserClub 설정
     */
    public UserClub applyClub(Long userId, ClubApplyRequest clubApplyRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        Club club  = clubRepository.findById(clubApplyRequest.getClubId())
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        //이미 해당하는 user와 club에 대한 user_club row가 존재한다면 duplicate error
        userClubRepository.findByUserAndClub(user, club)
                .ifPresent(uc->{
                    throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
                });

        // 클럽정원이 다 찼다면 가입 불가
        if( isClubFull(club) ){
            throw new CustomException(ErrorCode.CLUB_MEMBER_FULL);
        }

        UserClub userClub;
        // 관리자의 승인이 필요하지 않은 가입이라면 바로 APPROVED로 저장
        if ( club.getIsApproveRequired().equals("N") ){
            userClub = UserClub.builder()
                    .club(club)
                    .user(user)
                    .applyStatus(ApplyStatus.APPROVED)
                    .applyDate(LocalDateTime.now())
                    .approveDate(LocalDateTime.now())
                    .role(Role.MEMBER)
                    .updated(LocalDateTime.now())
                    .build();

            // approve로 인해 모집인원이 maxNumber와 같아진다면 모집 상태 자동 CLOSE 처리
            if(club.getRecruitNumber()+1==club.getMaxNumber()){
                club.setRecruitStatus(RecruitStatus.CLOSE);
            }
            clubRepository.save(club);

            // 바로 가입되는 가입요청의 경우 Action의 isProcessDone을 바로 true로 리
            Action action = Action.builder()
                    .actioner(user)
                    .actionClub(club)
                    .actionType(ActionType.APPLY)
                    .applyMessage(clubApplyRequest.getMemo())
                    .isProcessDone(true)
                    .created(LocalDateTime.now())
                    .build();

            actionRepository.save(action);

        } else { // 관리자의 승인이 필요한 가입이라면 APPLIED 상태로 저장
            userClub = UserClub.builder()
                    .club(club)
                    .user(user)
                    .applyDate(LocalDateTime.now())
                    .applyStatus(ApplyStatus.APPLIED)
                    .role(Role.PENDING)
                    .build();

            Action action = Action.builder()
                    .actioner(user)
                    .actionClub(club)
                    .actionType(ActionType.APPLY)
                    .applyMessage(clubApplyRequest.getMemo())
                    .isProcessDone(false)
                    .created(LocalDateTime.now())
                    .build();

            Action savedAction = actionRepository.save(action);

            List<User> adminList = getAdminList(club);
            adminList.forEach(
                    admin -> {
                        UserNotification userNotification = UserNotification.builder()
                                .action(action)
                                .recipient(admin)
                                .created(LocalDateTime.now())
                                .build();

                        userNotificationRepository.save(userNotification);


                        try {
                            if( admin.getUserPushAlarm()=='Y' && admin.getTargetToken()!=null){

                                Message fcmMessage = firebaseCloudMessageService.makeMessage(
                                        admin.getTargetToken(),
                                        "가입 요청",
                                        user.getName()+"님의 가입신청서가 도착했습니다.",
                                        club.getId(),
                                        savedAction.getId()
                                );

                                fcm.send(fcmMessage);
                            }
                        } catch (FirebaseMessagingException e) {
                            e.printStackTrace();
                        }

                    }
            );

            ClubNotification clubNotification = ClubNotification.builder()
                    .club(club)
                    .created(LocalDateTime.now())
                    .action(action)
                    .build();

            clubNotificationRepository.save(clubNotification);

        }

        return userClubRepository.save(userClub);
    }

    private List<User> getAdminList(Club club) {
        List<UserClub> userClubList = userClubRepository.findAllByClubId(club.getId());
        return userClubList.stream()
                .filter(userClub -> !userClub.getRole().equals(Role.MEMBER))
                .map(userClub -> userClub.getUser())
                .collect(Collectors.toList());

    }

    /**
     * 클럽 가입 승인 : userId, clubId에 해당하는 user_club row를 찾아 APPROVED로 변경
     */
    public void approveClub(Long approverId, Long approvedUserId, Long approvedClubId, Long actionId) {

        User approvedUser = userRepository.findById(approvedUserId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
        User approver  = userRepository.findById(approverId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
        Club club  = clubRepository.findById(approvedClubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );
        //user_id, club_id로 UserClub find
        UserClub approvedUserClub  = userClubRepository.findByUserAndClub(approvedUser, club)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND)
                );

        UserClub approverUserClub  = userClubRepository.findByUserAndClub(approver, club)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND)
                );

        Action processedAction  = actionRepository.findById(actionId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.ACTION_NOT_FOUND)
                );

        if( isClubFull(club) ){
            throw new CustomException(ErrorCode.CLUB_MEMBER_FULL);
        }

        if(approvedUserClub.getApplyStatus() == null || approvedUserClub.getApplyStatus()!=ApplyStatus.APPLIED){
            throw new CustomException(ErrorCode.USER_APPROVE_ERROR);
        }

        if(!approverUserClub.getRole().isCanApproveApply()){
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        approvedUserClub.setRole(Role.MEMBER);
        approvedUserClub.setApplyStatus(ApplyStatus.APPROVED);
        approvedUserClub.setApproveDate(LocalDateTime.now());
        approvedUserClub.setUpdated(LocalDateTime.now());

        userClubRepository.save(approvedUserClub);

        // approve로 인해 모집인원이 maxNumber와 같아진다면 모집 상태 자동 CLOSE 처리
        if(club.getRecruitNumber()+1==club.getMaxNumber()){
            club.setRecruitStatus(RecruitStatus.CLOSE);
        }
        clubRepository.save(club);

        actionRepository.findById(actionId);

        Action action = Action.builder()
                .actioner(approver)
                .actionee(approvedUser)
                .actionClub(club)
                .actionType(ActionType.APPROVE)
                .created(LocalDateTime.now())
                .build();

        actionRepository.save(action);

        // processDone 설정
        processedAction.setProcessDone(true);
        actionRepository.save(processedAction);

        UserNotification userNotification = UserNotification.builder()
                .action(action)
                .recipient(approvedUser)
                .created(LocalDateTime.now())
                .build();

        userNotificationRepository.save(userNotification);

        try {
            if( approvedUser.getUserPushAlarm()=='Y' && approvedUser.getTargetToken()!=null){

                Message fcmMessage = firebaseCloudMessageService.makeMessage(
                        approvedUser.getTargetToken(),
                        "가입 완료!",
                        club.getName()+"에 가입이 완료 되었습니다.",
                        club.getId(),
                        null);

                fcm.send(fcmMessage);
            }
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }

    }

    public void rejectAppliance(Long rejectorId, Long userId, Long clubId, Long actionId) {

        User rejector  = userRepository.findById(rejectorId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        User rejectedUser  = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        Club club  = clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        UserClub rejectedUserClub  = userClubRepository.findByUserAndClub(rejectedUser, club)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND)
                );

        UserClub rejectorUserClub  = userClubRepository.findByUserAndClub(rejector, club)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND)
                );

        Action processedAction  = actionRepository.findById(actionId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.ACTION_NOT_FOUND)
                );

        if(rejectedUserClub.getApplyStatus() == null || rejectedUserClub.getApplyStatus()!=ApplyStatus.APPLIED){
            throw new CustomException(ErrorCode.USER_APPROVE_ERROR);
        }

        if(!rejectorUserClub.getRole().isCanRejectApply()){
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        userClubRepository.delete(rejectedUserClub);

        Action action = Action.builder()
                .actioner(rejector)
                .actionee(rejectedUser)
                .actionClub(club)
                .actionType(ActionType.REJECT)
                .created(LocalDateTime.now())
                .build();

        actionRepository.save(action);

        processedAction.setProcessDone(true);
        actionRepository.save(processedAction);

        UserNotification userNotification = UserNotification.builder()
                .action(action)
                .recipient(rejectedUser)
                .created(LocalDateTime.now())
                .build();

        userNotificationRepository.save(userNotification);

        try {
            if( rejectedUser.getUserPushAlarm()=='Y' && rejectedUser.getTargetToken()!=null){

                Message fcmMessage = firebaseCloudMessageService.makeMessage(
                        rejectedUser.getTargetToken(),
                        "가입 거절",
                        club.getName()+"에 가입이 거절되었습니다.",
                        null,
                        null);

                fcm.send(fcmMessage);
            }
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }

    }

    public void likesClub(Long clubId, Long userId){

        Club club = clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        clubLikesRepository.findByClubAndUser(club,user)
                .ifPresentOrElse(
                        clubLikes -> {
                            if(clubLikes.isOnOff()){
                                clubLikes.setOnOff(false);
                            } else {
                                clubLikes.setOnOff(true);
                            }
                            clubLikesRepository.save(clubLikes);
                        },
                        () -> {
                            ClubLikes clubLikes = ClubLikes.builder()
                                    .club(club)
                                    .user(user)
                                    .onOff(true)
                                    .build();
                            clubLikesRepository.save(clubLikes);
                        }

                );

    }

    public ClubSchedule createClubSchedule(ClubScheduleCreateRequest clubScheduleCreateRequest, Long userId) {


        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        Club club = clubRepository.findById(clubScheduleCreateRequest.getClubId())
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );
        UserClub userClub = userClubRepository.findByUserAndClub(user, club)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND)
                );

        // user가 해당 클럽의 MANAGER || MASTER 인 경우에만 스케줄 생성 가능
        if(userClub.getRole().equals("MEMBER")){
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        LocalDateTime createdTime = LocalDateTime.now();

        // endDate 는 optional값이므로 null체크
        ClubSchedule clubSchedule = ClubSchedule.builder()
                .club(clubRepository.findById(clubScheduleCreateRequest.getClubId()).get())
                .name(clubScheduleCreateRequest.getName())
                .location(clubScheduleCreateRequest.getLocation())
                .content(clubScheduleCreateRequest.getContent())
                .startDate(clubScheduleCreateRequest.getStartDate())
                .endDate(
                        Optional.ofNullable(clubScheduleCreateRequest.getEndDate())
                        .orElse(null)
                )
                .created(createdTime)
                .build();

        return clubScheduleRepository.save(clubSchedule);
    }


    public ClubSchedule updateClubSchedule(ClubScheduleUpdateRequest clubScheduleUpdateRequest, Long clubId, Long scheduleId, Long userId) {

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

        ClubSchedule clubSchedule = clubScheduleRepository.findById(scheduleId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_SCHEDULE_NOT_FOUND)
                );

        if(userClub.getRole().equals("MEMBER")){
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        clubSchedule.setContent(
                Optional.ofNullable(clubScheduleUpdateRequest.getContent())
                        .orElse(clubSchedule.getContent())
        );
        clubSchedule.setStartDate(
                Optional.ofNullable(clubScheduleUpdateRequest.getStartDate())
                        .orElse(clubSchedule.getStartDate())
        );
        clubSchedule.setEndDate(
                Optional.ofNullable(clubScheduleUpdateRequest.getEndDate())
                        .orElse(clubSchedule.getEndDate())
        );
        clubSchedule.setName(
                Optional.ofNullable(clubScheduleUpdateRequest.getName())
                        .orElse(clubSchedule.getName())
        );
        clubSchedule.setLocation(
                Optional.ofNullable(clubScheduleUpdateRequest.getLocation())
                        .orElse(clubSchedule.getLocation())
        );
        clubSchedule.setUpdated(LocalDateTime.now());

        return clubScheduleRepository.save(clubSchedule);
    }

    public void deleteClubSchedule(Long clubId, Long scheduleId, Long userId) {

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

        if(userClub.getRole().equals(Role.MEMBER)){
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        clubScheduleRepository.deleteById(scheduleId);
    }


    public Header<List<ClubScheduleResponse>> selectClubScheduleList(Long clubId) {

        LocalDateTime now = LocalDateTime.now(); // 현재시간

        List<ClubSchedule> clubScheduleList = clubScheduleRepository.findByClubIdAndEndDateAfterOrderByStartDate(clubId, now);
        List<ClubScheduleResponse> clubScheduleResponseList = new ArrayList<>();

        for (ClubSchedule clubSchedule : clubScheduleList) {

            List<UserClubSchedule> userClubScheduleList = userClubScheduleRepository.findAll()
                    .stream()
                    .filter(ucs -> ucs.getClubSchedule().getId() == clubSchedule.getId())
                    .collect(Collectors.toList());

            List<UserResponse> members = new ArrayList<>();

            for (UserClubSchedule userClubSchedule : userClubScheduleList) {

                UserResponse userResponse = UserResponse.builder()
                        .id(userClubSchedule.getUser().getId())
                        .organizationName(userClubSchedule.getUser().getOrganization() != null ? userClubSchedule.getUser().getOrganization().getName() : null)
                        .name(userClubSchedule.getUser().getName())
                        .applyStatus(ApplyStatus.APPROVED)
                        .birthday(userClubSchedule.getUser().getBirthday())
                        .thumbnail(userClubSchedule.getUser().getThumbnail())
                        .sex(userClubSchedule.getUser().getSex())
                        .email(userClubSchedule.getUser().getEmail())
                        .created(userClubSchedule.getUser().getCreated())
                        .build();

                members.add(userResponse);
            }

            ClubScheduleResponse clubScheduleResponse = ClubScheduleResponse.builder()
                    .id(clubSchedule.getId())
                    .name(clubSchedule.getName())
                    .location(clubSchedule.getLocation())
                    .content(clubSchedule.getContent())
                    .members(members)
                    .startDate(clubSchedule.getStartDate())
                    .endDate(clubSchedule.getEndDate())
                    .build();

            clubScheduleResponseList.add(clubScheduleResponse);
        }

        return Header.OK(clubScheduleResponseList);
    }

    public UserClubSchedule joinOrCancelClubSchedule(Long clubId, Long scheduleId, Long userId) {

        ClubSchedule clubSchedule = clubScheduleRepository.findById(scheduleId).orElseThrow(
                () -> new CustomException(ErrorCode.CLUB_SCHEDULE_NOT_FOUND)
        );

        // clubSchedule이 속한 club의 id와 client가 요청한 club의 id가 일치하지 않으면 권한 에러
        if( isScheduleNotInClub(clubSchedule, clubId) ){
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }


        UserClubSchedule userClubSchedule = UserClubSchedule.builder()
                .user(userRepository.findById(userId)
                        .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND))
                )
                .clubSchedule(clubScheduleRepository.findById(scheduleId)
                        .orElseThrow(()-> new CustomException(ErrorCode.CLUB_SCHEDULE_NOT_FOUND))
                )
                .build();

       userClubScheduleRepository.findByUserIdAndClubScheduleId(userId, scheduleId)
                .ifPresentOrElse(
                        ucs -> {
                            userClubScheduleRepository.deleteById(ucs.getId());
                        },
                        () -> {
                            userClubScheduleRepository.save(userClubSchedule);
                        }
                );

       return userClubSchedule;
    }

    private boolean isClubFull(Club club) {
        return club.getMaxNumber()!=0 && club.getMaxNumber() <= club.getRecruitNumber();
    }

    private MyClubResponse selectMyClubResponse(Club club, User user){

        UserClub userClub = userClubRepository.findByUserAndClub(user,club)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.NO_PERMISSION)
                );

        List<ClubCategory> clubCategories = clubCategoryRepository.findByClub(club);
        List<CategoryResponse> categoryResponseList = new ArrayList<>();
        clubCategories.forEach(
                clubCategory -> {
                    categoryResponseList.add(selectCategoryResponse(clubCategory));
                }
        );

        MyClubResponse myClubResponse = MyClubResponse.builder()
                .id(club.getId())
                .name(club.getName())
                .clubShortDesc(club.getShortDesc())
                .clubLongDesc(club.getLongDesc())
                .organizationName(Optional.ofNullable(club.getOrganization())
                        .map(r-> r.getName())
                        .orElse(null))
                .thumbnail(club.getThumbnail())
                .categories(categoryResponseList)
                .applyStatus(userClub.getApplyStatus())
                .created(club.getCreated())
                .build();

        return myClubResponse;
    }

    private ClubResponse selectClubResponse(Club club){

        List<UserResponse> members = new ArrayList<>();


        if(club.getUserClubs()!=null){

            List<UserClub> userClubList = club.getUserClubs();

            for(UserClub userClub : userClubList){
                UserResponse userResponse = selectUserResponse(userClub.getUser(), club.getId());
                members.add(userResponse);
            }

        }


        List<ClubCategory> clubCategories = clubCategoryRepository.findByClub(club);
        List<CategoryResponse> categoryResponseList = new ArrayList<>();
        clubCategories.forEach(
                clubCategory -> {
                    categoryResponseList.add(selectCategoryResponse(clubCategory));
                }
        );

        ClubResponse clubResponse = ClubResponse.builder()
                .id(club.getId())
                .name(club.getName())
                .clubShortDesc(club.getShortDesc())
                .clubLongDesc(club.getLongDesc())
                .organizationName(Optional.ofNullable(club.getOrganization())
                        .map(r->r.getName())
                        .orElse(null))
                .members(members)
                .maxNumber(club.getMaxNumber())
                .thumbnail(club.getThumbnail())
                .recruitNumber(club.getRecruitNumber())
                .recruitStatus(club.getRecruitStatus())
                .isApprovedRequired(club.getIsApproveRequired())
                .categories(categoryResponseList)
                .contactPhone(club.getContactPhone())
                .created(club.getCreated())
                .build();

        return clubResponse;
    }

    private CategoryResponse selectCategoryResponse(ClubCategory clubCategory) {

        return CategoryResponse.builder()
                .id(clubCategory.getCategory().getId())
                .name(clubCategory.getCategory().getName())
                .description(clubCategory.getCategory().getDescription())
                .thumbnail(clubCategory.getCategory().getThumbnail())
                .order(clubCategory.getSortOrder())
                .build();
    }

    private UserResponse selectUserResponse(User user, Long clubId){

        UserClub userClub = user.getUserClubs()
                .stream()
                .filter(uc -> uc.getClub().getId() == clubId)
                .findAny()
                .orElse(null);

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .organizationName(Optional.ofNullable(user.getOrganization())
                        .map(Organization::getName)
                        .orElse(null))
                .thumbnail(user.getThumbnail())
                .name(user.getName())
                .birthday(user.getBirthday())
                .applyStatus(userClub.getApplyStatus()) //APPLIED, APPROVED
                .sex(user.getSex())
                .email(user.getEmail())
                .created(user.getCreated())
                .role(userClub.getRole())
                .build();

        return userResponse;

    }

    private Boolean hasNext(Long id) {
        if (id == null) return false;
        return clubRepository.existsByIdLessThan(id);
    }

    private Boolean isScheduleNotInClub(ClubSchedule clubSchedule, Long clubId){
        return !clubSchedule.getClub().getId().equals(clubId);
    }

    public Header<List<MyClubResponse>> selectMyClubs(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        List<MyClubResponse> myClubsResponse = userClubRepository.findAll()
                .stream()
                .filter(uc -> uc.getUser().getId() == userId && uc.getApplyStatus().equals(ApplyStatus.APPROVED))
                .map(uc -> uc.getClub())
                .distinct()
                .map(club -> selectMyClubResponse(club, user))
                .collect(Collectors.toList());

        return Header.OK(myClubsResponse);

    }

    public void withdrawClub(Long clubId, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        Club club = clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        UserClub userClub = userClubRepository.findByUserAndClub(user,club)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.NO_PERMISSION)
                );

        if( userClub.getApplyStatus().equals(ApplyStatus.APPLIED)){
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        if( userClub.getRole().equals(Role.MASTER) ){
            throw new CustomException(ErrorCode.MASTER_WITHDRAW_EXCEPTION);
        }

        userClubRepository.delete(userClub);

    }

    public void changeRole(Long approverId, Long clubId, List<UserAllocatedRole> changeRoleRequest) {

        User changer = userRepository.findById(approverId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        Club changeClub = clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        UserClub userClub = userClubRepository.findByUserAndClub(changer,changeClub)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.NO_PERMISSION)
                );

        if(!userClub.getRole().isCanAllocateRole()){
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        changeRoleRequest
                .forEach(ur-> allocateRole(ur.getUserId(),ur.getRole(), changeClub));

    }

    private void allocateRole(Long userId, Role role, Club changeClub) {

        User allocatedUser = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        UserClub userClub = userClubRepository.findByUserAndClub(allocatedUser,changeClub)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND)
                );

        if(role==null){
            userClubRepository.delete(userClub);
        } else {
            userClub.setRole(role);
            userClubRepository.save(userClub);
        }
    }

    public Header<DuplicateCheckResponse> duplicateCheck(String clubName) {
        Optional<Club> club = clubRepository.findByName(clubName);

        return club.map(c -> Header.OK(DuplicateCheckResponse.builder().isDuplicated('Y').build()))
                .orElse(Header.OK(DuplicateCheckResponse.builder().isDuplicated('N').build()));
    }

}
