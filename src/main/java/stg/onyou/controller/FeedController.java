package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.Reason;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.FeedImage;
import stg.onyou.model.entity.FeedSearch;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.FeedCreateRequest;
import stg.onyou.model.network.request.FeedUpdateRequest;
import stg.onyou.model.network.response.CommentResponse;
import stg.onyou.model.network.response.FeedResponse;
import stg.onyou.service.*;

import javax.servlet.http.HttpServletRequest;
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
    public Header<List<FeedResponse>> selectFeedList() {
        List<Feed> feeds = feedService.findFeeds();
        List<FeedResponse> resultList = feeds.stream()
                .map(f -> new FeedResponse(
                        f.getUser().getName(),
                        f.getContent(),
                        f.getLikes().size())
                )
                .collect(Collectors.toList());

        return Header.OK(resultList);
    }

    @GetMapping("/api/feeds/search")
    public Header<List<FeedResponse>> searchFeed(@RequestBody FeedSearch feedSearch) {
        List<Feed> feeds = feedService.findAllByString(feedSearch);
        List<FeedResponse> resultList = feeds.stream()
                .map(f -> new FeedResponse(
                        f.getUser().getName(),
                        f.getContent(),
                        f.getLikes().size())
                )
                .collect(Collectors.toList());
        return Header.OK(resultList);
    }

    @PostMapping("/api/feeds")
    public Header<Object> createFeed(@RequestPart(value = "file") List<MultipartFile> multipartFiles,
                                     @RequestPart(value = "feedCreateRequest") FeedCreateRequest request
                                     ) {

//        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        Long userId = 1L;
        List<FeedImage> feedImages = new ArrayList<>();
        Feed feed = feedService.createFeed(request, userId, feedImages);
        for (MultipartFile multipartFile : multipartFiles) {
            String url = awsS3Service.uploadFile(multipartFile);
            feedService.addFeedImage(feed, url);
        }
        hashtagService.addHashtagToFeed(feed);
        feedService.upload(feed);

        return Header.OK();
    }

    @GetMapping("/api/feeds/{id}")
    public Header<FeedResponse> selectFeed(@PathVariable Long id) {
        Feed feed = feedService.findById(id);
        FeedResponse result = new FeedResponse(
                feed.getUser().getName(),
                feed.getContent(),
                feed.getLikes().size()
        );

        return Header.OK(result);
    }

    @GetMapping("/api/clubs/{clubId}/feeds")
    public Header<List<FeedResponse>> selectFeedByClub(@PathVariable Long clubId) {
        List<Feed> feeds = feedService.findAllByClub(clubId);
        List<FeedResponse> resultList = feeds.stream()
                .map(f -> new FeedResponse(
                        f.getUser().getName(),
                        f.getContent(),
                        f.getLikes().size())
                )
                .collect(Collectors.toList());
        return Header.OK(resultList);
    }

    @PutMapping("/api/feeds/{id}")
    public Header<Object> updateFeed(@PathVariable Long id,
                                     @RequestBody FeedUpdateRequest request) {
        if (request.getAccess() == null || request.getContent() == null) {
            throw new CustomException(ErrorCode.FEED_UPDATE_ERROR);
        } else {
            feedService.updateFeed(id, request);
        }
        return Header.OK();
    }

//    @DeleteMapping("/api/feeds/{id}")
//    public Header<Object> deleteFeedBy(@PathVariable Long id) {
//        feedService.deleteById(id);
//        Feed feed = feedService.findById(id);
////        awsS3Service.deleteFile(feed.getFeedImages());
//        return Header.OK();
//    }

    /**
     * FEED 신고
     */
    @PutMapping("/api/feeds/{id}/report")
    public Header<String> reportFeed(@PathVariable Long id,
                           @RequestParam("reason") Reason reason, HttpServletRequest httpServletRequest) {
        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        String result = reportService.feedReport(userId, id, reason);
        return Header.OK(result);
    }


    /**
     * FEED 에 댓글 추가
     */
    @PostMapping("/api/feeds/{id}/comment")
    public Header<Object> commentFeed(@PathVariable Long id,
                                      @RequestBody Map<String, String> comment,
                                      HttpServletRequest httpServletRequest) {
        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        String content = comment.get("content");
        feedService.commentFeed(userId, id, content);
        return Header.OK();
    }

    @GetMapping("/api/feeds/{id}/comments")
    public Header<List<CommentResponse>> getCommentList(@PathVariable Long id) {
        return Header.OK(feedService.getComments(id));
    }

    @PostMapping("/api/feeds/{id}/likes")
    public Header<Object> likeFeed(@PathVariable Long id,
                                   HttpServletRequest httpServletRequest) {
        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        likesService.addLikes(userId, id);
        return Header.OK();
    }

    @PutMapping("/api/feeds/{id}/likes")
    public Header<Object> reverseLikeFeed(@PathVariable Long id,
                                          HttpServletRequest httpServletRequest) {
        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        likesService.reversLikes(userId, id);
        return Header.OK();
    }

    @GetMapping("/api/feeds/{id}/likes")
    public Header<Integer> getFeedLikesCount(@PathVariable Long id) {
        return Header.OK(feedService.getLikesCount(id));
    }

}