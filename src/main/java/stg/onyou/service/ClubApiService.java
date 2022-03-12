package stg.onyou.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.ClubCreateRequest;
import stg.onyou.model.network.request.UserApiRequest;
import stg.onyou.model.network.response.ClubApiResponse;
import stg.onyou.model.network.response.UserApiResponse;
import stg.onyou.repository.ClubRepository;
import stg.onyou.repository.UserRepository;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClubApiService {

    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private UserRepository userRepository;

    public Header<ClubApiResponse> selectClub(Integer id){

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
            new CustomException(ErrorCode.CLUB_NOT_FOUND);
        }
        return Header.OK(clubs);
    }

  /*  private Header<ClubApiResponse> createClub(ClubCreateRequest clubCreateRequest){

        clubCreateRequest.getCategoryId()
        userRepository.findById(clubCreateRequest.getCreatorId())
                .map(tempUser -> user = tempUser)


    clubCreateRequest.getCreatorId();

        private Organization organization;
        private User creator;
        Club club = Club.builder()
                .name(clubCreateRequest.getClubName())
                .delYn('N')
                .thumbnail("default image url")
                .recruitStatus("BEGIN")
                .maxNumber(clubCreateRequest.getClubMaxMember())
                .created(LocalDateTime.now())
                .category()

        clubCreateRequest;
        create
        clubCreateRequest
    }*/


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
                .information(club.getInformation())
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
                .birthdate(user.getBirthdate())
                .sex(user.getSex())
                .accountEmail(user.getAccountEmail())
                .created(user.getCreated())
                .build();

        return userApiResponse;

    }

}
