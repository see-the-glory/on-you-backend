package stg.onyou.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stg.onyou.model.AccessModifier;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.FeedSearch;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.FeedCreateRequest;
import stg.onyou.model.network.request.FeedUpdateRequest;
import stg.onyou.model.network.response.FeedResponse;
import stg.onyou.service.AwsS3Service;
import stg.onyou.service.ClubService;
import stg.onyou.service.FeedService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Api(tags = {"Feed API Controller"})
@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final ClubService clubService;
    private final AwsS3Service awsS3Service;

    @GetMapping("/api/feeds")
    public Header<List<FeedResponse>> selectFeedList() {
        List<Feed> feeds = feedService.findFeeds();
        List<FeedResponse> resultList = feeds.stream()
                .map(f -> new FeedResponse(f.getUser().getName(), f.getContent()))
                .collect(Collectors.toList());
        return Header.OK(resultList);
    }


    @GetMapping("/api/feeds/search")
    public List<Feed> searchFeed(@RequestParam FeedSearch feedSearch) {
        return feedService.findAllByString(feedSearch);
    }

    @PostMapping("/api/feed")
    public Header<Object> createFeed(
            @RequestPart List<MultipartFile> multipartFile, HttpServletRequest httpServletRequest) {

//        Feed feed = Feed.builder()
//                .content(request.getContent())
//                .delYn('n')
//                .access(AccessModifier.PUBLIC)
//                .created(LocalDateTime.now())
//                .updated(LocalDateTime.now())
////                .club()
////                .user()
//                .build();

//        feedService.upload(feed);
        //Long userId = 1L;

        Long userId = Long.parseLong(httpServletRequest.getAttribute("userId").toString());
        awsS3Service.uploadFile(multipartFile, userId);
        return Header.OK();
    }

    @GetMapping("/api/feed/{id}")
    public Feed selectFeed(@PathVariable Long id) {
        return feedService.findById(id);
    }

    @GetMapping("/api/{clubId}/feeds")
    public Header<List<FeedResponse>> selectFeedByClub(@PathVariable Long clubId){
        List<Feed> feeds = feedService.findAllByClub(clubId);
        List<FeedResponse> resultList = feeds.stream()
                .map(f -> new FeedResponse(f.getUser().getName(), f.getContent()))
                .collect(Collectors.toList());
        return Header.OK(resultList);
    }

    @PutMapping("/api/feed/{id}")
    public Header<Object> updateFeed(@PathVariable Long id,
                                     @RequestBody FeedUpdateRequest request) {
        feedService.updateFeed(id, request);
        return Header.OK();
    }

    @DeleteMapping ("/api/feed/{id}")
    public Header<Object> deleteFeedBy(@PathVariable Long id) {
        feedService.deleteById(id);
        return Header.OK();
    }

    @PutMapping("/api/report/{id}")
    public void reportFeed(@PathVariable Long id) {
        feedService.reportFeed(id);
    }

}