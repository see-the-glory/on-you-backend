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
import stg.onyou.model.network.response.ClubApiResponse;
import stg.onyou.model.network.response.UserApiResponse;
import stg.onyou.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClubApiService {

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

    public Header<ClubApiResponse> selectClub(Long id){

        return clubRepository.findById(id)
                .map(club -> Header.OK(selectClubResponse(club)))
                .orElseThrow(
                        ()-> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );
    }

    public Header<List<ClubApiResponse>> selectAllClubs(){

        List<ClubApiResponse> clubs = new ArrayList<ClubApiResponse>();

        clubRepository.findAll()
            .forEach(club->{
                clubs.add(selectClubResponse(club));
        });

        if(clubs.isEmpty()){
            throw new CustomException(ErrorCode.CLUB_NOT_FOUND);
        }
        return Header.OK(clubs);
    }

    public Club createClub(ClubCreateRequest clubCreateRequest){

        Club club = Club.builder()
                .name(clubCreateRequest.getClubName())
                .short_desc(clubCreateRequest.getClubShortDesc())
                .long_desc(clubCreateRequest.getClubLongDesc())
                .delYn('N')
                .thumbnail("default image url")
                .recruitStatus(RecruitStatus.BEGIN)
                .maxNumber(clubCreateRequest.getClubMaxMember())
                .created(LocalDateTime.now())
                .category(categoryRepository.findById(clubCreateRequest.getCategoryId()).get())
                .organization(organizationRepository.findById(1L).get())
                .creator(userRepository.findById(1L).get())
                .build();

        return clubRepository.save(club);

    }

    public UserClub applyClub(Long userId, Long clubId) {

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

        UserClub userClub = UserClub.builder()
                .club(club)
                .user(user)
                .applyStatus(ApplyStatus.APPLIED)
                .build();

        return userClubRepository.save(userClub);
    }

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


        UserClub userClub = UserClub.builder()
                .club(club)
                .user(user)
                .applyStatus(ApplyStatus.APPROVED)
                .build();

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

    private ClubApiResponse selectClubResponse(Club club){

        List<UserApiResponse> members = new ArrayList<UserApiResponse>();
        int recruitNumber;

        club.getUserClubs()
                .forEach(userClub -> {
                    members.add(selectUserResponse(userClub.getUser()));
                 });
        recruitNumber = club.getUserClubs().size();

        //private String applyStatus; //AVAILABLE, APPLIED, APPROVED
        ClubApiResponse clubApiResponse = ClubApiResponse.builder()
                .id(club.getId())
                .name(club.getName())
                .clubShortDesc(club.getShort_desc())
                .clubLongDesc(club.getLong_desc())
                .announcement(club.getAnnouncement())
                .organizationName(club.getOrganization().getName())
                .members(members)
                .maxNumber(club.getMaxNumber())
                .thumbnail(club.getThumbnail())
                .recruitNumber(recruitNumber)
                .recruitStatus(club.getRecruitStatus())
                .categoryName(club.getCategory().getName())
                .creatorName(club.getCreator().getName())
                // applyStatus도 이 api서 현재 사용자 id값을 url에 포함시켜서 받아와야 할지. 아니면 별도의 api로 가져와야 할지.
                .build();

        return clubApiResponse;
    }

    private UserApiResponse selectUserResponse(User user){

        UserApiResponse userApiResponse = UserApiResponse.builder()
                .id(user.getId())
                .organizationName(user.getOrganization().getName())
                .birthday(user.getBirthday())
                .sex(user.getSex())
                .email(user.getEmail())
                .created(user.getCreated())
                .build();

        return userApiResponse;

    }

}
