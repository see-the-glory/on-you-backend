package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.CommentCreateRequest;
import stg.onyou.model.network.request.FeedCreateRequest;
import stg.onyou.model.network.request.ReportRequest;
import stg.onyou.model.network.request.FeedUpdateRequest;
import stg.onyou.model.network.response.*;
import stg.onyou.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Api(tags = {"Feed API Controller"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final ClubService clubService;
    private final AwsS3Service awsS3Service;
    private final UserService userService;
    //    private final CommentService commentService;
    private final LikesService likesService;
    private final ReportService reportService;
    private final HashtagService hashtagService;

    @GetMapping("/api/feeds")
    public FeedPageResponse selectFeedList(
            @RequestParam(required = false) String cursor,
            @PageableDefault(sort="created", size = 9) Pageable pageable,
            HttpServletRequest httpServletRequest) {

        Long userId = userService.getUserId(httpServletRequest);
        return feedService.selectFeedList(pageable, cursor, userId);

    }

    @GetMapping("/api/clubs/{clubId}/feeds")
    public FeedPageResponse selectFeedListByClub(
            @PathVariable Long clubId,
            @RequestParam(required = false) String cursor,
            @PageableDefault(sort="created", size = 9) Pageable pageable,
            HttpServletRequest httpServletRequest) {

        Long userId = userService.getUserId(httpServletRequest);
        return feedService.selectFeedListByClub(pageable, cursor, userId, clubId);

    }

    @GetMapping("/api/users/{userId}/feeds")
    public FeedPageResponse selectFeedListByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) String cursor,
            @PageableDefault(sort="created", size = 9) Pageable pageable,
            HttpServletRequest httpServletRequest) {

        Long myId = userService.getUserId(httpServletRequest);
        return feedService.selectFeedListByUser(pageable, cursor, userId, myId);

    }

    @GetMapping("/api/feeds/my")
    public FeedPageResponse getMyFeedList(
            @RequestParam(required = false) String cursor,
            @PageableDefault(sort="created", size = 9) Pageable pageable,
            HttpServletRequest httpServletRequest) {

        Long userId = userService.getUserId(httpServletRequest);
        return feedService.getMyFeedList(pageable, cursor, userId);

    }



    @PostMapping("/api/feeds")
    public Header<Object> createFeed(@RequestPart(value = "file") List<MultipartFile> multipartFiles,
                                     @RequestPart(value = "feedCreateRequest") @Valid FeedCreateRequest request,
                                     HttpServletRequest httpServletRequest
    ) {

        log.debug("FeedCreateRequest: ", request);
        log.debug("multipartFiles: ", multipartFiles);

        Long userId = userService.getUserId(httpServletRequest);
        List<FeedImage> feedImages = new ArrayList<>();
        Feed feed = feedService.createFeed(request, userId, feedImages);
        for (MultipartFile multipartFile : multipartFiles) {
            String url = awsS3Service.uploadFile(multipartFile);
            feedService.addFeedImage(feed, url);
        }
        List<FeedHashtag> feedHashtagList = hashtagService.addHashtagToFeed(feed);
        feed.setFeedHashtags(feedHashtagList);
        feedService.upload(feed);

        return Header.OK("Feed 생성 완료");
    }

    @GetMapping("/api/feeds/{feedId}")
    public Header<FeedLinkResponse> getFeed(@PathVariable Long feedId, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return feedService.getFeed(userId, feedId);
    }

    @PutMapping("/api/feeds/{id}")
    public Header<Object> updateFeed(@PathVariable Long id,
                                     @RequestBody FeedUpdateRequest request,
                                     HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        Feed feed = feedService.findById(id);

        if (!feed.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NO_AUTH_UPDATE_FEED);
        }

        if (request.getAccess() == null || request.getContent() == null) {
            throw new CustomException(ErrorCode.FEED_UPDATE_ERROR);
        } else {
            feedService.updateFeed(id, request);
            return Header.OK("Feed 수정 완료");
        }

    }

    @DeleteMapping("/api/feeds/{id}")
    public Header<Object> deleteFeed(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        Feed feed = feedService.findById(id);

        feedService.deleteById(id, userId);
//        Feed feed = feedService.findById(id);
//        awsS3Service.deleteFile(feed.getFeedImages());
        return Header.OK("Feed 삭제 완료");
    }

    /**
     * FEED 신고
     */
    @PostMapping("/api/feeds/{id}/report")
    public Header<String> reportFeed(@PathVariable Long id, @Valid @RequestBody ReportRequest reportRequest, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);

        String result = reportService.reportFeed(userId, id, reportRequest.getReason());
        return Header.OK(result);
    }

    /**
     * FEED 에 댓글 추가
     */
    @PostMapping("/api/feeds/{feedId}/comment")
    public Header<String> createComment(@PathVariable Long feedId,
                                      @Valid @RequestBody CommentCreateRequest commentCreateRequest,
                                      HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        feedService.createComment(userId, feedId, commentCreateRequest);
        return Header.OK("댓글 등록 완료");
    }

    @GetMapping("/api/feeds/{id}/comments")
    public Header<List<CommentResponse>> getCommentList(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        return Header.OK(feedService.getComments(id, userId));
    }

    @GetMapping("/api/comments/{id}/likeUserList")
    public Header<List<LikeUserResponse>> getCommentLikeUserList(@PathVariable Long id) {
        return Header.OK(feedService.getCommentLikeUserList(id));
    }

    @GetMapping("/api/feeds/{id}/likeUserList")
    public Header<List<LikeUserResponse>> getFeedLikeUserList(@PathVariable Long id) {
        return Header.OK(feedService.getFeedLikeUserList(id));
    }

    @PostMapping("/api/feeds/{id}/likes")
    public Header<Object> likeFeed(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        likesService.addLikesFeed(userId, id);
        return Header.OK();
    }

    @GetMapping("/api/feeds/{id}/likes")
    public Header<Integer> getFeedLikesCount(@PathVariable Long id) {
        return Header.OK(feedService.getLikesCount(id));
    }

}