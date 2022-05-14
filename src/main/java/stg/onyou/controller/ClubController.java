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
import stg.onyou.model.network.response.ClubResponse;
import stg.onyou.service.ClubService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Api(tags = {"Club API Controller"})
@Slf4j
@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Autowired
    private ClubService clubService;

    @GetMapping("/{id}")
    public Header<ClubResponse> selectClub(@PathVariable Long id){
        return clubService.selectClub(id);
    }

    @GetMapping("")
    public Header<List<ClubResponse>> selectAllClubs(){
        return clubService.selectAllClubs();
    }

    @PostMapping("")
    public Header<String> createClub(@Valid @RequestBody ClubCreateRequest clubCreateRequest){

        Club club = clubService.createClub(clubCreateRequest);
        if(club == null){
            throw new CustomException(ErrorCode.CLUB_CREATION_ERROR);
        }
        return Header.OK("club id: "+ club.getId());
    }

    @PostMapping("/{id}/apply")
    public Header<String> applyClub(@PathVariable Long id, HttpServletRequest httpServletRequest){

        // JwtAuthorizationFilter에서 jwt를 검증해서 얻은 userId를 가져온다.
        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());

        UserClub userClub = clubService.applyClub(userId,id);
        if(userClub == null){
            throw new CustomException(ErrorCode.CLUB_REGISTER_ERROR);
        }
        return Header.OK("user_id: "+ userClub.getUser().getId()+", club_id: "+userClub.getClub().getId());
    }

    @PostMapping("/{id}/approve")
    public Header<String> registerClub(@PathVariable Long id){

        UserClub userClub = clubService.approveClub(1L,id);
        if(userClub == null){
            throw new CustomException(ErrorCode.CLUB_REGISTER_ERROR);
        }
        return Header.OK("user id: "+ userClub.getUser().getId()+",club id: "+userClub.getClub().getId());
    }
}
