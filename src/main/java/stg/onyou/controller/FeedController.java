package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Comment;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.FeedImage;
import stg.onyou.model.entity.FeedSearch;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.FeedCreateRequest;
import stg.onyou.model.network.request.FeedUpdateRequest;
import stg.onyou.model.network.response.CommentResponse;
import stg.onyou.model.network.response.FeedResponse;
//import stg.onyou.repository.LikesRepository;
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
//    private final LikesRepository likesRepository;

    @GetMapping("/api/feeds")
    public Header<List<FeedResponse>> selectFeedList() {
        List<Feed> feeds = feedService.findFeeds();
        List<FeedResponse> resultList = feeds.stream()
                .map(f -> new FeedResponse(
                        f.getUser().getName(),
                        f.getContent(),
                        f.getComments().stream().map(c -> new CommentResponse(c.getUser().getName(), c.getContent(), c.getCreated(), c.getUpdated())).collect(Collectors.toList()),
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
                        f.getComments().stream().map(c -> new CommentResponse(c.getUser().getName(), c.getContent(), c.getCreated(), c.getUpdated())).collect(Collectors.toList()),
                        f.getLikes().size())
                )
                .collect(Collectors.toList());
        return Header.OK(resultList);
    }

    @PostMapping("/api/feeds")
    public Header<Object> createFeed(@RequestPart(value = "file") List<MultipartFile> multipartFiles,
                                     @RequestPart(value = "feedCreateRequest") FeedCreateRequest request,
                                     HttpServletRequest httpServletRequest) {

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        List<FeedImage> feedImages = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String url = awsS3Service.uploadFile(multipartFile);

            feedService.addFeedImage(feedImages, url); // TODO test
        }

        Feed feed = feedService.getFeed(request, userId, feedImages);

        feedService.upload(feed);

        return Header.OK();
    }

    @GetMapping("/api/feeds/{id}")
    public Header<FeedResponse> selectFeed(@PathVariable Long id) {
        Feed feed = feedService.findById(id);
        FeedResponse result = new FeedResponse(
                feed.getUser().getName(),
                feed.getContent(),
                feed.getComments().stream().map(c -> new CommentResponse(c.getUser().getName(), c.getContent(), c.getCreated(), c.getUpdated())).collect(Collectors.toList()),
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
                        f.getComments().stream().map(c -> new CommentResponse(c.getUser().getName(), c.getContent(), c.getCreated(), c.getUpdated())).collect(Collectors.toList()),
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

    @PutMapping("/api/feeds/{id}/report")
    public void reportFeed(@PathVariable Long id) {
        feedService.reportFeed(id);
    }

//    @PostMapping("/api/feeds/{id}/like")
//    public Header<Object> likeFeed(@PathVariable Long id,
//                                   HttpServletRequest httpServletRequest) {
////        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
//        Long userId = 1L;
//
//        // 기존에 좋아요 기록이 없으면, crate
//        if (likesRepository.findByUserId(userId) == null) {
//            feedService.createLikeFeed(userId, id);
//        } else {
//            // 좋아요 기록이 있으면, update like flip
//            feedService.updateLikeFeed()
//        }
//
//
//        return Header.OK();
//    }


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
    public Header<List<CommentResponse>> getCommentList(@PathVariable Long id){
        return Header.OK(feedService.getComments(id));
    }

}