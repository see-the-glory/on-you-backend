package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.*;
import stg.onyou.model.network.response.*;
import stg.onyou.service.AwsS3Service;
import stg.onyou.service.ClubService;
import stg.onyou.service.UserService;

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
    @Autowired
    private UserService userService;

    private final Integer DEFAULT_PAGINATION_SIZE = 5;



    @GetMapping("/{id}")
    public Header<ClubResponse> selectClub(@PathVariable Long id){
        return clubService.selectClub(id);
    }

    @GetMapping("/my")
    public Header<List<MyClubResponse>> selectMyClubs(HttpServletRequest httpServletRequest){
        Long userId = userService.getUserId(httpServletRequest);
        return clubService.selectMyClubs(userId);
    }

    @GetMapping("")
    public ClubPageResponse selectClubList(
            
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "DESC", required = false) String orderBy,
        @RequestParam(defaultValue = "0", required = false) Long categoryId,
        @RequestParam(defaultValue = "0", required = false) int showRecruitingOnly,
        @RequestParam(defaultValue = "0", required = false) int showMy,
        @RequestParam(defaultValue = "0", required = false) int min,
        @RequestParam(defaultValue = "1000", required = false) int max,
        @PageableDefault (sort="created", size = 8) Pageable pageable,
        HttpServletRequest httpServletRequest){

        Long userId = userService.getUserId(httpServletRequest);

        ClubCondition clubCondition = ClubCondition.builder()
                .orderBy(orderBy)
                .categoryId(categoryId)
                .showRecruitingOnly(showRecruitingOnly)
                .showMy(showMy)
                .min(min)
                .max(max)
                .build();

        return clubService.selectClubList(pageable, clubCondition, cursor, userId);
    }

    @GetMapping("/{id}/role")
    public Header<ClubRoleResponse> selectClubRole(@PathVariable Long id, HttpServletRequest httpServletRequest){
        Long userId = userService.getUserId(httpServletRequest);
        return clubService.selectClubRole(id, userId);
    }

    @PostMapping("")
    public Header<ClubResponse> createClub(@RequestPart(value = "file", required = false) MultipartFile thumbnail,
                                     @Valid @RequestPart(value = "clubCreateRequest")
                                             ClubCreateRequest clubCreateRequest,
                                     HttpServletRequest httpServletRequest){

        Long userId = userService.getUserId(httpServletRequest);

        if(thumbnail.isEmpty()){
            throw new CustomException(ErrorCode.ClUB_IMAGE_REQUIRED);
        }

        String thumbnailUrl = awsS3Service.uploadFile(thumbnail); //s3에 저장하고 저장한 image url 리턴
        clubCreateRequest.setThumbnailUrl(thumbnailUrl);

        return clubService.createClub(clubCreateRequest, userId);
    }

    @PutMapping("/{id}")
    public Header<ClubResponse> updateClub(@PathVariable Long id, @RequestPart(value = "file", required = false) MultipartFile thumbnail,
                                     @Valid @RequestPart(value = "clubUpdateRequest")
                                             ClubUpdateRequest clubUpdateRequest,
                                     HttpServletRequest httpServletRequest){

        if(thumbnail != null){
            String thumbnailUrl = awsS3Service.uploadFile(thumbnail);
            clubUpdateRequest.setThumbnailUrl(thumbnailUrl);
        }

        return clubService.updateClub(clubUpdateRequest, id);
    }

    @PostMapping("/{clubId}/withdraw")
    public Header<String> withdrawClub(@PathVariable Long clubId, HttpServletRequest httpServletRequest){

        Long userId = userService.getUserId(httpServletRequest);
        clubService.withdrawClub(clubId, userId);

        return Header.OK("user_id: "+userId+"club_id: "+clubId);
    }

    @DeleteMapping("/{clubId}")
    public Header<String> deleteClub(@PathVariable Long clubId, HttpServletRequest httpServletRequest){

        Long userId = userService.getUserId(httpServletRequest);
        clubService.deleteClub(clubId, userId);
        return Header.OK("Deleted successfully");
    }


    @PostMapping("/apply")
    public Header<String> applyClub(@RequestBody ClubApplyRequest clubApplyRequest, HttpServletRequest httpServletRequest){

        Long userId = userService.getUserId(httpServletRequest);

        UserClub userClub = clubService.applyClub(userId, clubApplyRequest);
        return Header.OK("신청 완료");
    }

    @PostMapping("/approve")
    public Header<String> approveClub(@RequestBody ClubApproveRequest clubApproveRequest, HttpServletRequest httpServletRequest){

        Long approverId = userService.getUserId(httpServletRequest);
        clubService.approveClub(approverId, clubApproveRequest.getUserId(), clubApproveRequest.getClubId(), clubApproveRequest.getActionId());

        return Header.OK("승인 완료");
    }

    @PostMapping("/reject")
    public Header<String> rejectAppliance(@RequestBody ClubRejectRequest clubRejectRequest, HttpServletRequest httpServletRequest){

        Long rejectorId = userService.getUserId(httpServletRequest);
        clubService.rejectAppliance(rejectorId, clubRejectRequest.getUserId(), clubRejectRequest.getClubId(), clubRejectRequest.getActionId());

        return Header.OK("승인 완료");
    }

    @PostMapping("/{clubId}/changeRole")
    public Header<String> changeRole(@PathVariable Long clubId, @RequestBody List<UserAllocatedRole> changeRoleRequest, HttpServletRequest httpServletRequest){
        Long approverId = userService.getUserId(httpServletRequest);
        clubService.changeRole(approverId, clubId, changeRoleRequest);

        return Header.OK("권한변경 및 탈퇴처리 완료");
    }

    @PostMapping("/{clubId}/likes")
    public Header<String> likesClub(@PathVariable Long clubId, HttpServletRequest httpServletRequest){

        Long userId = userService.getUserId(httpServletRequest);
        clubService.likesClub(clubId, userId);

        return Header.OK("Likes 등록 또는 해제 완료");
    }

    @GetMapping("/{id}/schedules")
    public Header<List<ClubScheduleResponse>> selectClubScheduleList(@PathVariable Long id, HttpServletRequest httpServletRequest){

        return clubService.selectClubScheduleList(id);

    }

    @PostMapping("/schedules")
    public Header<String> createClubSchedule(@Valid @RequestBody ClubScheduleCreateRequest clubScheduleCreateRequest, HttpServletRequest httpServletRequest){

        Long userId = userService.getUserId(httpServletRequest);

        ClubSchedule clubSchedule = clubService.createClubSchedule(clubScheduleCreateRequest, userId);
        if(clubSchedule == null){
            throw new CustomException(ErrorCode.CLUB_SCHEDULE_MUTATION_ERROR);
        }

        return Header.OK("club_schedule_id: "+ clubSchedule.getId());

    }

    @PutMapping("/{clubId}/schedules/{scheduleId}")
    public Header<String> updateClubSchedule(@PathVariable Long clubId, @PathVariable Long scheduleId, @Valid @RequestBody ClubScheduleUpdateRequest clubScheduleUpdateRequest,
        HttpServletRequest httpServletRequest){

        Long userId = userService.getUserId(httpServletRequest);

        ClubSchedule clubSchedule = clubService.updateClubSchedule(clubScheduleUpdateRequest, clubId, scheduleId, userId);
        if(clubSchedule == null){
            throw new CustomException(ErrorCode.CLUB_SCHEDULE_MUTATION_ERROR);
        }

        return Header.OK("club_schedule_id: "+ clubSchedule.getId());

    }

    @DeleteMapping("/{clubId}/schedules/{scheduleId}")
    public Header<String> deleteClubSchedule(@PathVariable Long clubId, @PathVariable Long scheduleId, HttpServletRequest httpServletRequest){

        Long userId = userService.getUserId(httpServletRequest);

        clubService.deleteClubSchedule(clubId, scheduleId, userId);
        return Header.OK("Deleted successfully");
    }

    @PostMapping("/{clubId}/schedules/{scheduleId}/joinOrCancel")
    public Header<String> joinOrCancelClubSchedule(@PathVariable Long clubId, @PathVariable Long scheduleId, HttpServletRequest httpServletRequest){

        Long userId = userService.getUserId(httpServletRequest);

        UserClubSchedule userClubSchedule = clubService.joinOrCancelClubSchedule(clubId, scheduleId, userId);

        return Header.OK("user_id: "+userClubSchedule.getUser().getId()+", club_schedule_id: "+ userClubSchedule.getClubSchedule().getId());

    }

    @PostMapping("/duplicateCheck")
    public Header<DuplicateCheckResponse> duplicateCheck(@RequestBody DuplicateCheckRequest duplicateCheckRequest){

        return userService.duplicateCheck(duplicateCheckRequest.getClubName());

    }

}
