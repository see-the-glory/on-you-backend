package stg.onyou.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.UserClub;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.UserApiRequest;
import stg.onyou.model.network.response.ClubApiResponse;
import stg.onyou.model.network.response.UserApiResponse;
import stg.onyou.repository.ClubRepository;
import stg.onyou.repository.UserRepository;
import stg.onyou.model.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClubApiService {

    @Autowired
    private ClubRepository clubRepository;

    public Header<ClubApiResponse> selectClub(Integer id){

        return clubRepository.findById(id)
                .map(club -> response(club))
                .orElseThrow(
                        ()-> new CustomException(ErrorCode.CLUB_NOT_FOUND)
                );
    }

    public Header<List<ClubApiResponse>> selectAllClubs(){

        List<ClubApiResponse> clubs = new ArrayList<ClubApiResponse>();

        clubRepository.findAll()
            .forEach(club->{
                List<UserApiResponse> members = new ArrayList<UserApiResponse>();
                int recruitNumber = club.getUserClubs().size();
                club.getUserClubs()
                        .forEach(userClub -> {
                            UserApiResponse user = UserApiResponse.builder()
                                    .id(userClub.getUser().getId())
                                    .organizationName(userClub.getUser().getOrganization().getName())
                                    .birthdate(userClub.getUser().getBirthdate())
                                    .sex(userClub.getUser().getSex())
                                    .accountEmail(userClub.getUser().getAccountEmail())
                                    .created(userClub.getUser().getCreated())
                                    .build();

                            members.add(user);
                        });

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
                        .build();

                clubs.add(clubApiResponse);
        });

        return Header.OK(clubs);
    }

    private Header<ClubApiResponse> response(Club club){

        List<UserApiResponse> members = new ArrayList<UserApiResponse>();
        int recruitNumber;

        club.getUserClubs()
                .forEach(userClub -> {  //요청받은 club id값을 지닌 모든 user_club의 row에 대해

            UserApiResponse user = UserApiResponse.builder()
                .id(userClub.getUser().getId())
//                .organizationName(userClub.getUser().getOrganization().getName())
                .birthdate(userClub.getUser().getBirthdate())
                .sex(userClub.getUser().getSex())
                .accountEmail(userClub.getUser().getAccountEmail())
                .created(userClub.getUser().getCreated())
                .build();

            members.add(user);
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

        return Header.OK(clubApiResponse);

    }

}
