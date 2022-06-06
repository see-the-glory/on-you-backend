package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.*;
import stg.onyou.model.network.response.ClubResponse;
import stg.onyou.model.network.response.ClubScheduleResponse;
import stg.onyou.service.AwsS3Service;
import stg.onyou.service.ClubService;
import stg.onyou.service.CursorResult;

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
    @Autowired
    private AwsS3Service awsS3Service;

    private final Integer DEFAULT_PAGINATION_SIZE = 2;

    @GetMapping("/{id}")
    public Header<ClubResponse> selectClub(@PathVariable Long id){
        return clubService.selectClub(id);
    }

    @GetMapping("")
    public Header<CursorResult<ClubResponse>> selectClubList(Long cursorId, Long category1Id, Long category2Id){

        CursorResult<ClubResponse> clubs = clubService.selectClubList(cursorId, PageRequest.of(0, DEFAULT_PAGINATION_SIZE), category1Id, category2Id);

        return Header.OK(clubs);
    }

    @PostMapping("")
    public Header<String> createClub(@RequestPart(value = "file", required = false) MultipartFile thumbnail,
                                     @Valid @RequestPart(value = "clubCreateRequest")
                                             ClubCreateRequest clubCreateRequest,
                                     HttpServletRequest httpServletRequest){

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());

        if(!thumbnail.isEmpty()){
            String thumbnailUrl = awsS3Service.uploadFile(thumbnail); //s3에 저장하고 저장한 image url 리턴
            clubCreateRequest.setThumbnailUrl(thumbnailUrl);
        }

        Club club = clubService.createClub(clubCreateRequest, userId);

        return Header.OK("club_id: "+ club.getId());
    }

    @PutMapping("/{id}")
    public Header<Club> updateClub(@PathVariable Long id, @RequestPart(value = "file", required = false) MultipartFile thumbnail,
                                     @Valid @RequestPart(value = "clubUpdateRequest")
                                             ClubUpdateRequest clubUpdateRequest,
                                     HttpServletRequest httpServletRequest){

        if(!thumbnail.isEmpty()){
            String thumbnailUrl = awsS3Service.uploadFile(thumbnail);
            clubUpdateRequest.setThumbnailUrl(thumbnailUrl);
        }

        return clubService.updateClub(clubUpdateRequest, id);
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
    public Header<String> approveClub(@PathVariable Long id, HttpServletRequest httpServletRequest){

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());

        UserClub userClub = clubService.approveClub(userId,id);
        if(userClub == null){
            throw new CustomException(ErrorCode.CLUB_REGISTER_ERROR);
        }
        return Header.OK("user_id: "+ userClub.getUser().getId()+",club_id: "+userClub.getClub().getId());
    }


    @PostMapping("/{id}/allocate")
    public Header<String > allocateUserClubRole(@PathVariable Long id, @RequestBody ClubRoleAllocateRequest clubRoleAllocateRequest){
        UserClub userClub  = clubService.allocateUserClubRole(clubRoleAllocateRequest, id);

        return Header.OK("user_id: "+ userClub.getUser().getId()+",club_id: "+userClub.getClub().getId());
    }


    @GetMapping("/{id}/schedules")
    public Header<List<ClubScheduleResponse>> selectClubScheduleList(@PathVariable Long id, HttpServletRequest httpServletRequest){

        return clubService.selectClubScheduleList(id);

    }

    @PostMapping("/schedules")
    public Header<String> createClubSchedule(@Valid @RequestBody ClubScheduleCreateRequest clubScheduleCreateRequest, HttpServletRequest httpServletRequest){

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());

        ClubSchedule clubSchedule = clubService.createClubSchedule(clubScheduleCreateRequest, userId);
        if(clubSchedule == null){
            throw new CustomException(ErrorCode.CLUB_SCHEDULE_MUTATION_ERROR);
        }

        return Header.OK("club_schedule_id: "+ clubSchedule.getId());

    }

    @PutMapping("/schedules/{id}")
    public Header<String> updateClubSchedule(@PathVariable Long id, @Valid @RequestBody ClubScheduleUpdateRequest clubScheduleUpdateRequest){

        ClubSchedule clubSchedule = clubService.updateClubSchedule(clubScheduleUpdateRequest, id);
        if(clubSchedule == null){
            throw new CustomException(ErrorCode.CLUB_SCHEDULE_MUTATION_ERROR);
        }

        return Header.OK("club_schedule_id: "+ clubSchedule.getId());

    }

    @PostMapping("/schedules/{id}/register")
    public Header<String> registerClubSchedule(@PathVariable Long id, HttpServletRequest httpServletRequest){

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());

        UserClubSchedule userClubSchedule = clubService.registerClubSchedule(id, userId);
        if(userClubSchedule == null){
            throw new CustomException(ErrorCode.CLUB_SCHEDULE_MUTATION_ERROR);
        }

        return Header.OK("user_id: "+userClubSchedule.getUser().getId()+", club_schedule_id: "+ userClubSchedule.getClubSchedule().getId());

    }

}
