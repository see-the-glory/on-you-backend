package stg.onyou.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.ApplyStatus;
import stg.onyou.model.RecruitStatus;
import stg.onyou.model.Role;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.*;
import stg.onyou.model.network.response.*;
import stg.onyou.repository.*;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClubService {

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
    private UserClubApplicationRepository userClubApplicationRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ClubQRepository clubQRepository;
    @Autowired
    private ClubCategoryRepository clubCategoryRepository;
//    @Autowired
//    EntityManager em;

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

    public Page<ClubConditionResponse> selectClubs(int page, ClubSearchRequest clubSearchRequest) {
        //페이징 조건과 정렬 조건을 같이 보내 준다.
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.fromString(clubSearchRequest.getOrderBy()), clubSearchRequest.getSortType()));
        return clubQRepository.findClubSearchList(pageRequest, clubSearchRequest);
    }

//    public void selectClubs(){
//
//        RecruitStatus rs = null;
//        Integer minMemberNum = 1;
//        Integer maxMemberNum = 9;
//        OrderByCriteria orderByCriteria = OrderByCriteria.CLUB_CREATED;
//        String orderBy = "ASC";
//
//        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
//
//        QClub club = QClub.club;
//        BooleanBuilder builder = new BooleanBuilder();
//        if(rs != null){
//            builder.and(club.recruitStatus.eq(rs.RECRUIT));
//        }
//        if(minMemberNum != null && maxMemberNum != null){
//            builder.and(club.maxNumber.between(minMemberNum,maxMemberNum));
//        }
//        if(orderByCriteria != null){
//            if (orderByCriteria.equals(OrderByCriteria.CLUB_CREATED)) {
//                builder.and(club.created)
//            } else if (orderByCriteria.equals(OrderByCriteria.MEMBER_NUM)){
//
//            } else if (orderByCriteria.equals(OrderByCriteria.FEED_NUM)) {
//
//            } else {
//
//            }
//        }

//        List<Club> findClubs = queryFactory.selectFrom(club)
//                .where(builder)
//                .orderBy(clubSort())
//                .fetch();
//
//        System.out.println(findClubs);
//    }


    /**
     * 전체 클럽 select
     */
//    public CursorResult<ClubResponse> selectClubList(Long cursorId, Pageable page, Long category1Id, Long category2Id, String searchKeyword) {
//
//        List<ClubResponse> clubs = new ArrayList<>();
//
//        getClubList(cursorId, page, category1Id, category2Id)
//            .forEach(club->{
//                clubs.add(selectClubResponse(club));
//            });
//
//        final Long lastIdOfList = clubs.isEmpty() ?
//                null : clubs.get(clubs.size() - 1).getId();
//
//        return new CursorResult<>(clubs, hasNext(lastIdOfList));
//    }

    public Header<ClubRoleResponse> selectClubRole(Long clubId, Long userId){

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

        ClubRoleResponse clubRoleResponse = ClubRoleResponse.builder()
                .userId(userClub.getUser().getId())
                .clubId(userClub.getClub().getId())
                .role(userClub.getRole())
                .build();

        return Header.OK(clubRoleResponse);

    }

//    public Header<ClubMessagesResponse> selectClubMessages(Long clubId, Long userId){
//
//
//    }

    /**
     * 클럽 create
     */
    public Header<ClubResponse> createClub(ClubCreateRequest clubCreateRequest, Long userId){

        // category1과 category2가 같은 것을 선택하는 것 방지
        if(clubCreateRequest.getCategory2Id()==clubCreateRequest.getCategory1Id()){
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }

        // 1. club 저장 : clubLongDesc는 optional값이므로 null체크
        Club club = Club.builder()
                .name(clubCreateRequest.getClubName())
                .shortDesc(clubCreateRequest.getClubShortDesc())
                .longDesc(
                        Optional.ofNullable(clubCreateRequest.getClubLongDesc())
                            .orElse(null)
                )
                .delYn('N')
                .thumbnail("default image url")
                .recruitStatus(RecruitStatus.RECRUIT)
                .maxNumber(clubCreateRequest.getClubMaxMember())
                .isApproveRequired(clubCreateRequest.getIsApproveRequired())
                .created(LocalDateTime.now())
                .thumbnail(clubCreateRequest.getThumbnailUrl())
                .organization(organizationRepository.findById(1L).get())
                .creator(userRepository.findById(userId)
                    .orElseThrow(
                            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                    )
                )
                .build();

        Club savedClub = clubRepository.save(club);

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

        // 3. club_category 저장
        ClubCategory clubCategory1 = ClubCategory.builder()
                .club(savedClub)
                .category(categoryRepository.findById(clubCreateRequest.getCategory1Id())
                        .orElseThrow(
                                () -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
                        )
                )
                .created(LocalDateTime.now())
                .build();

        clubCategoryRepository.save(clubCategory1);

        if(clubCreateRequest.getCategory2Id() != null){
            ClubCategory clubCategory2 = ClubCategory.builder()
                    .club(savedClub)
                    .category(categoryRepository.findById(clubCreateRequest.getCategory2Id())
                            .orElseThrow(
                                    () -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
                            )
                    )
                    .created(LocalDateTime.now())
                    .build();

            clubCategoryRepository.save(clubCategory2);
        }

        // 4. 저장 성공한 club의 reponse 생성하여 return
        ClubResponse clubResponse = ClubResponse.builder()
                .id(savedClub.getId())
                .name(savedClub.getName())
                .clubShortDesc(savedClub.getShortDesc())
                .clubLongDesc(savedClub.getLongDesc())
                .organizationName(savedClub.getOrganization().getName())
                .maxNumber(savedClub.getMaxNumber())
                .thumbnail(savedClub.getThumbnail())
                .recruitStatus(savedClub.getRecruitStatus())
                .creatorName(savedClub.getCreator().getName())
                .build();

        return Header.OK(clubResponse);

    }

    public Header<Club> updateClub(ClubUpdateRequest clubUpdateRequest, Long clubId) {

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
        club.setUpdated(LocalDateTime.now());

        return Header.OK(clubRepository.save(club));
    }

    /**
     * 클럽 가입요청 : 디폴트로 APPLIED 상태로 UserClub 설정
     */
    public UserClub applyClub(Long userId, Long clubId, ClubApplyRequest clubApplyRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        Club club  = clubRepository.findById(clubId)
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
                    .build();

        } else { // 관리자의 승인이 필요한 가입이라면 APPLIED 상태로 저장
            userClub = UserClub.builder()
                    .club(club)
                    .user(user)
                    .applyDate(LocalDateTime.now())
                    .applyStatus(ApplyStatus.APPLIED)
                    .build();
        }


        return userClubRepository.save(userClub);
    }

    /**
     * 클럽 가입 승인 : userId, clubId에 해당하는 user_club row를 찾아 APPROVED로 변경
     */
    public UserClub approveClub(Long approverId, Long approvedUserId, Long clubId) {

        User approvedUser = userRepository.findById(approvedUserId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
        User approver  = userRepository.findById(approverId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );
        Club club  = clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        if( isClubFull(club) ){
            throw new CustomException(ErrorCode.CLUB_MEMBER_FULL);
        }

        //user_id, club_id로 UserClub find
        UserClub approvedUserClub  = userClubRepository.findByUserAndClub(approvedUser, club)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND)
                );

        UserClub approverUserClub  = userClubRepository.findByUserAndClub(approver, club)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND)
                );

        if(approvedUserClub.getApplyStatus() == null || approvedUserClub.getApplyStatus()!=ApplyStatus.APPLIED){
            throw new CustomException(ErrorCode.USER_APPOVE_ERROR);
        }

        if(!approverUserClub.getRole().equals(Role.MEMBER)){
            approvedUserClub.setRole(Role.MEMBER);
            approvedUserClub.setApplyStatus(ApplyStatus.APPROVED);
            approvedUserClub.setApproveDate(LocalDateTime.now());
        }

        return userClubRepository.save(approvedUserClub);
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
                .created(LocalDateTime.now())
                .build();

        return clubScheduleRepository.save(clubSchedule);
    }


    public ClubSchedule updateClubSchedule(ClubScheduleUpdateRequest clubScheduleUpdateRequest, Long id) {
        ClubSchedule clubSchedule = clubScheduleRepository.findById(id)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_SCHEDULE_NOT_FOUND)
                );

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


    public Header<List<ClubScheduleResponse>> selectClubScheduleList(Long id) {

        List<ClubSchedule> clubScheduleList = clubScheduleRepository.findAll()
                .stream()
                .filter(cs -> cs.getClub().getId() == id)
                .collect(Collectors.toList());

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
                        .organizationName(userClubSchedule.getUser().getOrganization().getName())
                        .name(userClubSchedule.getUser().getName())
                        .applyStatus(ApplyStatus.APPROVED)
                        .birthday(userClubSchedule.getUser().getBirthday())
                        .sex(userClubSchedule.getUser().getSex())
                        .email(userClubSchedule.getUser().getAccount_email())
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

    public UserClubSchedule registerClubSchedule(Long clubScheduleId, Long userId) {

        UserClubSchedule userClubSchedule = UserClubSchedule.builder()
                .user(userRepository.findById(userId)
                        .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND))
                )
                .clubSchedule(clubScheduleRepository.findById(clubScheduleId)
                        .orElseThrow(()-> new CustomException(ErrorCode.CLUB_SCHEDULE_NOT_FOUND))
                )
                .build();

        return userClubScheduleRepository.save(userClubSchedule);
    }

    public void cancelClubSchedule(Long clubScheduleId, Long userId) {

        UserClubSchedule userClubSchedule = userClubScheduleRepository.findByUserIdAndClubScheduleId(userId, clubScheduleId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_CLUB_SCHEDULE_NOT_FOUND)
                );

       userClubScheduleRepository.deleteById(userClubSchedule.getId());
    }

    public UserClub allocateUserClubRole(ClubRoleAllocateRequest clubRoleAllocateRequest, Long clubId) {

        User user = userRepository.findById(clubRoleAllocateRequest.getUserId())
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

        userClub.setRole(clubRoleAllocateRequest.getRole());
        return userClubRepository.save(userClub);
    }

    private boolean isClubFull(Club club) {

        // return club.getMaxNumber() <= userClubRepository.findAllByClubId(club.getId()).size();
        int a = club.getMaxNumber();
        Long b = userClubRepository.findAllByClubId(club.getId())
                .stream()
                .filter(item->item.getApplyStatus() == ApplyStatus.APPROVED)
                .count();

        //b= userClubRepository.findAllByClubId(club.getId())
        //  .stream()
        //.filter(item -> item.getApplyStatus() == ApplyStatus.APPROVED)
        //.count();

        return a!=0 && a <= b ;
    }

    private ClubResponse selectClubResponse(Club club){

        List<UserResponse> members = new ArrayList<>();

        club.getUserClubs()
                .forEach(userClub -> {
                    members.add(selectUserResponse(userClub.getUser(), club.getId()));
                });

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
                .categories(categoryResponseList)
                .creatorName(Optional.ofNullable(club.getCreator())
                        .map(r -> r.getName())
                        .orElse(null)
                )
                .build();

        return clubResponse;
    }

    private CategoryResponse selectCategoryResponse(ClubCategory clubCategory) {

        return CategoryResponse.builder()
                .id(clubCategory.getCategory().getId())
                .name(clubCategory.getCategory().getName())
                .description(clubCategory.getCategory().getDescription())
                .thumbnail(clubCategory.getCategory().getThumbnail())
                .order(clubCategory.getOrder())
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
                .organizationName(user.getOrganization().getName())
                .name(user.getName())
                .birthday(user.getBirthday())
                .applyStatus(userClub.getApplyStatus()) //APPLIED, APPROVED
                .sex(user.getSex())
                .email(user.getAccount_email())
                .created(user.getCreated())
                .build();

        return userResponse;

    }

    private Boolean hasNext(Long id) {
        if (id == null) return false;
        return clubRepository.existsByIdLessThan(id);
    }

}
