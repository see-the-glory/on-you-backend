package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.UserClub;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.ClubCreateRequest;
import stg.onyou.model.network.response.ClubApiResponse;
import stg.onyou.service.ClubApiService;

import java.util.List;

@Api(tags = {"Club API Controller"})
@Slf4j
@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Autowired
    private ClubApiService clubApiService;

    @GetMapping("/{id}")
    public Header<ClubApiResponse> selectClub(@PathVariable Integer id){
        return clubApiService.selectClub(id);
    }

    @GetMapping("")
    public Header<List<ClubApiResponse>> selectAllClubs(){
        return clubApiService.selectAllClubs();
    }

    @PostMapping("")
    public Header<String> createClub(@RequestBody ClubCreateRequest clubCreateRequest){

        Club club = clubApiService.createClub(clubCreateRequest);
        if(club == null){
            throw new CustomException(ErrorCode.CLUB_CREATION_ERROR);
        }
        return Header.OK("club id: "+ club.getId());
    }

    @PostMapping("/{id}/apply")
    public Header<String> applyClub(@PathVariable Integer id){

        UserClub userClub = clubApiService.applyClub(1,id);
        if(userClub == null){
            throw new CustomException(ErrorCode.CLUB_REGISTER_ERROR);
        }
        return Header.OK("user id: "+ userClub.getUser().getId()+",club id: "+userClub.getClub().getId());
    }

    @PostMapping("/{id}/approve")
    public Header<String> registerClub(@PathVariable Integer id){

        UserClub userClub = clubApiService.approveClub(1,id);
        if(userClub == null){
            throw new CustomException(ErrorCode.CLUB_REGISTER_ERROR);
        }
        return Header.OK("user id: "+ userClub.getUser().getId()+",club id: "+userClub.getClub().getId());
    }

    /*
    @PostMapping("{id}/register")
    public*/
}
