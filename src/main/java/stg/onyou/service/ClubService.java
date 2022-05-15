package stg.onyou.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.ApplyStatus;
import stg.onyou.model.RecruitStatus;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.ClubCreateRequest;
import stg.onyou.model.network.response.ClubResponse;
import stg.onyou.model.network.response.UserResponse;
import stg.onyou.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public Header<List<ClubResponse>> selectAllClubs(){

        List<ClubResponse> clubs = new ArrayList<ClubResponse>();

        clubRepository.findAll()
            .forEach(club->{
                clubs.add(selectClubResponse(club));
        });

        if(clubs.isEmpty()){
            throw new CustomException(ErrorCode.CLUB_NOT_FOUND);
        }
        return Header.OK(clubs);
    }

    /**
     * 클럽 create
     */
    public Club createClub(ClubCreateRequest clubCreateRequest, Long userId){

        Category category2;
        if(clubCreateRequest.getCategory2Id()==null){
            category2 = null;
        } else {
            category2 = categoryRepository.findById(clubCreateRequest.getCategory2Id()).get();
        }

        String clubLongDesc;
        if(clubCreateRequest.getClubLongDesc()==null){
            clubLongDesc = null;
        } else {
            clubLongDesc = clubCreateRequest.getClubLongDesc();
        }
        Club club = Club.builder()
                .name(clubCreateRequest.getClubName())
                .short_desc(clubCreateRequest.getClubShortDesc())
                .long_desc(clubLongDesc)
                .delYn('N')
                .thumbnail("default image url")
                .recruitStatus(RecruitStatus.BEGIN)
                .maxNumber(clubCreateRequest.getClubMaxMember())
                .created(LocalDateTime.now())
                .category1(categoryRepository.findById(clubCreateRequest.getCategory1Id()).get())
                .category2(category2)
                .organization(organizationRepository.findById(1L).get())
                .creator(userRepository.findById(userId)
                    .orElseThrow(
                            () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                    )
                )
                .build();


        return clubRepository.save(club);

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
                .ifPresent(m->{
                    throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
                });

        // 클럽정원이 다 찼다면 가입 불가
        if( isClubFull(club) ){
            throw new CustomException(ErrorCode.CLUB_MEMBER_FULL);
        }

        UserClub userClub = UserClub.builder()
                .club(club)
                .user(user)
                .applyStatus(ApplyStatus.APPLIED)
                .build();

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

        userClub.setApplyStatus(ApplyStatus.APPROVED);

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

        return a <= b;
    }

    private ClubResponse selectClubResponse(Club club){

        List<UserResponse> members = new ArrayList<>();
        int recruitNumber;

        club.getUserClubs()
                .forEach(userClub -> {
                    members.add(selectUserResponse(userClub.getUser()));
                 });
        recruitNumber = club.getUserClubs().size();


        //private String applyStatus; //AVAILABLE, APPLIED, APPROVED
        ClubResponse clubResponse = ClubResponse.builder()
                .id(club.getId())
                .name(club.getName())
                .clubShortDesc(club.getShort_desc())
                .clubLongDesc(club.getLong_desc())
                .announcement(club.getAnnouncement())
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

    private UserResponse selectUserResponse(User user){

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .organizationName(user.getOrganization().getName())
                .birthday(user.getBirthday())
                .sex(user.getSex())
                .email(user.getEmail())
                .created(user.getCreated())
                .build();

        return userResponse;

    }

}
