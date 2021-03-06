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
import stg.onyou.model.network.response.ClubRoleResponse;
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

    private final Integer DEFAULT_PAGINATION_SIZE = 5;

    @GetMapping("/{id}")
    public Header<ClubResponse> selectClub(@PathVariable Long id){
        return clubService.selectClub(id);
    }

    @GetMapping("")
    public Header<CursorResult<ClubResponse>> selectClubList(Long cursorId, Long category1Id, Long category2Id, String searchKeyword){

        CursorResult<ClubResponse> clubs = clubService.selectClubList(cursorId, PageRequest.of(0, DEFAULT_PAGINATION_SIZE), category1Id, category2Id, searchKeyword);

        return Header.OK(clubs);
    }

    @GetMapping("/{id}/role")
    public Header<ClubRoleResponse> selectClubRole(@PathVariable Long id, HttpServletRequest httpServletRequest){
        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        return clubService.selectClubRole(id, userId);
    }

//    @GetMapping("/{id}")
//    public Header<ClubApplierResponse> selectClubMessages(@PathVariable Long id, HttpServletRequest httpServletRequest){
//        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
//        return clubService.selectClubMessages(id, userId);
//    }


    @PostMapping("")
    public Header<ClubResponse> createClub(@RequestPart(value = "file", required = false) MultipartFile thumbnail,
                                     @Valid @RequestPart(value = "clubCreateRequest")
                                             ClubCreateRequest clubCreateRequest,
                                     HttpServletRequest httpServletRequest){

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());

        if(!thumbnail.isEmpty()){
            String thumbnailUrl = awsS3Service.uploadFile(thumbnail); //s3??? ???????????? ????????? image url ??????
            clubCreateRequest.setThumbnailUrl(thumbnailUrl);
        }

        return clubService.createClub(clubCreateRequest, userId);
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
    public Header<String> applyClub(@PathVariable Long id, @RequestBody ClubApplyRequest clubApplyRequest, HttpServletRequest httpServletRequest){

        // JwtAuthorizationFilter?????? jwt??? ???????????? ?????? userId??? ????????????.
        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());

        UserClub userClub = clubService.applyClub(userId,id);
        if(userClub == null){
            throw new CustomException(ErrorCode.CLUB_REGISTER_ERROR);
        }
        return Header.OK("user_id: "+ userClub.getUser().getId()+", club_id: "+userClub.getClub().getId());
    }

    @PostMapping("/{id}/approve")
    public Header<String> approveClub(@PathVariable Long id, @RequestBody ClubApproveRequest clubApproveRequest, HttpServletRequest httpServletRequest){

        Long approverId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        UserClub userClub = clubService.approveClub(approverId, clubApproveRequest.getApprovedUserId(), id);
        if(userClub == null){
            throw new CustomException(ErrorCode.CLUB_REGISTER_ERROR);
        }
        return Header.OK("user_id: "+ userClub.getUser().getId()+",club_id: "+userClub.getClub().getId());
    }

    @PostMapping("/{id}/likes")
    public Header<String> likesClub(@PathVariable Long id, HttpServletRequest httpServletRequest){

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        clubService.likesClub(id, userId);

        return Header.OK("Likes ?????? ?????? ?????? ??????");
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

    @PutMapping("/{clubId}/schedules/{scheduleId}")
    public Header<String> updateClubSchedule(@PathVariable Long clubId, @PathVariable Long scheduleId, @Valid @RequestBody ClubScheduleUpdateRequest clubScheduleUpdateRequest,
        HttpServletRequest httpServletRequest){

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());

        ClubSchedule clubSchedule = clubService.updateClubSchedule(clubScheduleUpdateRequest, clubId, scheduleId, userId);
        if(clubSchedule == null){
            throw new CustomException(ErrorCode.CLUB_SCHEDULE_MUTATION_ERROR);
        }

        return Header.OK("club_schedule_id: "+ clubSchedule.getId());

    }

    @DeleteMapping("/{clubId}/schedules/{scheduleId}")
    public Header<String> deleteClubSchedule(@PathVariable Long clubId, @PathVariable Long scheduleId, HttpServletRequest httpServletRequest){

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());

        clubService.deleteClubSchedule(clubId, scheduleId, userId);
        return Header.OK("Deleted successfully");
    }

    @PostMapping("/{clubId}/schedules/{scheduleId}/joinOrCancel")
    public Header<String> joinOrCancelClubSchedule(@PathVariable Long clubId, @PathVariable Long scheduleId, HttpServletRequest httpServletRequest){

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());

        UserClubSchedule userClubSchedule = clubService.joinOrCacnelClubSchedule(clubId, scheduleId, userId);

        return Header.OK("user_id: "+userClubSchedule.getUser().getId()+", club_schedule_id: "+ userClubSchedule.getClubSchedule().getId());

    }

//    @DeleteMapping("{clubId}/schedules/{scheduleId}/cancel")
//    public Header<String> cancelClubSchedule(@PathVariable Long clubId, @PathVariable Long scheduleId, HttpServletRequest httpServletRequest){
//
//        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
//
//        clubService.cancelClubSchedule(clubId, scheduleId, userId);
//
//        return Header.OK("Deleted successfully");
//
//    }

}
