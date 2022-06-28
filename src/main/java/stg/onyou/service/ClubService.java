package stg.onyou.service;

import org.hibernate.validator.constraints.Range;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.ApplyStatus;
import stg.onyou.model.RecruitStatus;
import stg.onyou.model.Role;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.*;
import stg.onyou.model.network.response.CategoryResponse;
import stg.onyou.model.network.response.ClubResponse;
import stg.onyou.model.network.response.ClubScheduleResponse;
import stg.onyou.model.network.response.UserResponse;
import stg.onyou.repository.*;

import javax.validation.constraints.*;
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
    private ModelMapper modelMapper;

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

    /**
     * 전체 클럽 select
     */
    public CursorResult<ClubResponse> selectClubList(Long cursorId, Pageable page, Long category1Id, Long category2Id, String searchKeyword) {

        List<ClubResponse> clubs = new ArrayList<>();

        getClubList(cursorId, page, category1Id, category2Id)
            .forEach(club->{
                clubs.add(selectClubResponse(club));
            });

        final Long lastIdOfList = clubs.isEmpty() ?
                null : clubs.get(clubs.size() - 1).getId();

        return new CursorResult<>(clubs, hasNext(lastIdOfList));
    }

    /**
     * 클럽 create
     */
    public Header<ClubResponse> createClub(ClubCreateRequest clubCreateRequest, Long userId){

        // category1과 category2가 같은 것을 선택하는 것 방지
        if(clubCreateRequest.getCategory2Id()==clubCreateRequest.getCategory1Id()){
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }

        // clubLongDesc, category2Id 는 optional값이므로 null체크
        Club club = Club.builder()
                .name(clubCreateRequest.getClubName())
                .shortDesc(clubCreateRequest.getClubShortDesc())
                .longDesc(
                        Optional.ofNullable(clubCreateRequest.getClubLongDesc())
                            .orElse(null)
                )
                .delYn('N')
                .thumbnail("default image url")
                .recruitStatus(RecruitStatus.BEGIN)
                .maxNumber(clubCreateRequest.getClubMaxMember())
                .isApproveRequired(clubCreateRequest.getIsApproveRequired())
                .created(LocalDateTime.now())
                .category1(categoryRepository.findById(clubCreateRequest.getCategory1Id()).get())
                .category2(
                        Optional.ofNullable(clubCreateRequest.getCategory2Id())
                                .map(r -> categoryRepository.findById(r).get())
                                .orElse(null)
                )
                .thumbnail(clubCreateRequest.getThumbnailUrl())
                .organization(organizationRepository.findById(1L).get())
                .creator(userRepository.findById(userId)
                    .orElseThrow(
                            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                    )
                )
                .build();

        Club savedClub = clubRepository.save(club);
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
                .category1Name(savedClub.getCategory1().getName())
                .category2Name(
                        Optional.ofNullable(savedClub.getCategory2())
                            .map(r -> r.getName())
                            .orElse(null)
                )
                .build();

        return Header.OK(clubResponse);

    }

    public Header<Club> updateClub(ClubUpdateRequest clubUpdateRequest, Long clubId) {

        Club club = clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        club.setCategory1(
                Optional.ofNullable(categoryRepository.findById(clubUpdateRequest.getCategory1Id()).get())
                    .orElse(club.getCategory1())
        );
        club.setCategory1(
                Optional.ofNullable(categoryRepository.findById(clubUpdateRequest.getCategory2Id()).get())
                        .orElse(club.getCategory2())
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
    public UserClub applyClub(Long userId, Long clubId) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        Club club  = clubRepository.findById(clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );

        //이미 해당하는 user와 club에 대한 user_club row가 존재한다면 duplicate error
        userClubRepository.findByUserIdAndClubId(userId, clubId)
                .ifPresent(uc->{
                    throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
                });

        // 클럽정원이 다 찼다면 가입 불가
        if( isClubFull(club) ){
            throw new CustomException(ErrorCode.CLUB_MEMBER_FULL);
        }

        UserClub userClub;
        // 관리자의 승인이 필요하지 않은 가입이라면 바로 APPROVED로 저장
        if ( club.getIsApproveRequired().equals("Y") ){
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
    public UserClub approveClub(Long userId, Long clubId) {

        User user = userRepository.findById(userId)
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
        UserClub userClub  = userClubRepository.findByUserIdAndClubId(userId, clubId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_CLUB_NOT_FOUND)
                );

        if(userClub.getApplyStatus() == null || userClub.getApplyStatus()!=ApplyStatus.APPLIED){
            throw new CustomException(ErrorCode.USER_APPOVE_ERROR);
        }
        userClub.setRole(Role.MEMBER);
        userClub.setApplyStatus(ApplyStatus.APPROVED);
        userClub.setApproveDate(LocalDateTime.now());

        return userClubRepository.save(userClub);
    }

    public ClubSchedule createClubSchedule(ClubScheduleCreateRequest clubScheduleCreateRequest, Long userId) {

        UserClub userClub = userClubRepository.findByUserIdAndClubId(userId, clubScheduleCreateRequest.getClubId())
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

        List<ClubScheduleResponse> clubScheduleResponseList = clubScheduleList.stream()
                .map(clubSchedule -> modelMapper.map(clubSchedule, ClubScheduleResponse.class))
                .collect(Collectors.toList());

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

    public UserClub allocateUserClubRole(ClubRoleAllocateRequest clubRoleAllocateRequest, Long clubId) {

        UserClub userClub = userClubRepository.findByUserIdAndClubId(clubRoleAllocateRequest.getUserId(), clubId)
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
        int recruitNumber;

        club.getUserClubs()
                .forEach(userClub -> {
                    members.add(selectUserResponse(userClub.getUser(), club.getId()));
                });
        recruitNumber = club.getUserClubs().size();


        //private String applyStatus; //AVAILABLE, APPLIED, APPROVED
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
                .recruitNumber(recruitNumber)
                .recruitStatus(club.getRecruitStatus())
                .category1Name(Optional.ofNullable(club.getCategory1())
                        .map(r -> r.getName())
                        .orElse(null)
                )
                .category2Name(Optional.ofNullable(club.getCategory2())
                        .map(r -> r.getName())
                        .orElse(null)
                )
                .creatorName(Optional.ofNullable(club.getCreator())
                        .map(r -> r.getName())
                        .orElse(null)
                )
                // applyStatus도 이 api서 현재 사용자 id값을 url에 포함시켜서 받아와야 할지. 아니면 별도의 api로 가져와야 할지.
                .build();

        return clubResponse;
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
                .applyStatus(userClub.getApplyStatus())
                .sex(user.getSex())
                .email(user.getEmail())
                .created(user.getCreated())
                .build();

        return userResponse;

    }

    private List<Club> getClubList(Long id, Pageable page, Long category1Id, Long category2Id) {

        if (id == null) { // cursor-id 가 존재하지 않는 경우에 대하여, ( initial 호출 )
            if (category1Id == null && category2Id == null) { //category 필터 존재 x
                return clubRepository.findAllByOrderByIdDesc(page);
            } else if (category1Id != null && category2Id != null) { //category filter 2개 존재하면 category1,2순서 관계없이 각각 find해서 Union
                List<Club> joined = new ArrayList<>();
                joined.addAll(clubRepository.findByCategory1IdAndCategory2IdOrderByIdDesc(category1Id, category2Id, page));
                joined.addAll(clubRepository.findByCategory1IdAndCategory2IdOrderByIdDesc(category2Id, category1Id, page));
                return joined;
            } else { // category filter 1개만 존재
                return clubRepository.findByCategory1IdOrCategory2IdOrderByIdDesc(category1Id, category1Id, page);
            }
        } else { // cursor-id 가 존재하는 경우에 대하여, ( 2번째 이상의 호출 )
            if (category1Id == null && category2Id == null) { //category 필터 존재 X
                return clubRepository.findByIdLessThanOrderByIdDesc(id, page);
            } else if (category1Id != null && category2Id != null) { //category filter 2개 존재하면 category1,2순서 관계없이 각각 find해서 Union
                List<Club> joined = new ArrayList<>();
                joined.addAll(clubRepository.findByCategory1IdAndCategory2IdLessThanOrderByIdDesc(category1Id, category2Id, id, page));
                joined.addAll(clubRepository.findByCategory1IdAndCategory2IdLessThanOrderByIdDesc(category2Id, category1Id, id, page));
                return joined;
            } else { // category filter 1개만 존재
                return clubRepository.findByCategory1IdOrCategory2IdAndIdLessThanOrderByIdDesc(category1Id, category1Id, id, page);
            }
        }
    }

    private Boolean hasNext(Long id) {
        if (id == null) return false;
        return clubRepository.existsByIdLessThan(id);
    }

}
