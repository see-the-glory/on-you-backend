package stg.onyou.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stg.onyou.model.entity.Feed;
import stg.onyou.service.FeedService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feeds")
public class FeedController {

    private final FeedService feedService;
    private final String publicAccess = "public";  // public (전체 공개)

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }


    @GetMapping("")
    public List<Feed> getPublicFeedList() {
        return feedService.findFeeds(publicAccess);
    }

    @GetMapping("/recent")
    public List<Feed> getPublicFeedListOrderByCreated() {
        return feedService.findFeedsOrderByCreated(publicAccess);
    }

    @GetMapping("")
    public List<Feed> searchFeedByTitleOrContent(@RequestParam String title, @RequestParam String content) {
        return feedService.findAllByTitleOrContent(title, content);
    }

    @PostMapping("")
    public String createFeed(@RequestBody Feed feedForm) {
        Feed feed = new Feed();
        feed.setTitle(feedForm.getTitle());
        feed.setContent(feedForm.getContent());
        feed.setAccess(feedForm.getAccess());
        feed.setCreated(LocalDateTime.now());

        feed.setGroupId(1);
        feed.setUserId(1);
        feedService.upload(feed);
        return "redirect:/";
    }

    @GetMapping("/{id}")
    public Optional<Feed> getFeedInfo(@PathVariable int id) {
        return feedService.findById(id);
    }

    @PutMapping("/{id}")
    public void updateFeed(@PathVariable int id,
                           @RequestBody Feed updateFeed) {
        feedService.updateFeed(id, updateFeed);
    }

    @PutMapping("/{id}")
    public void deleteFeedById(@PathVariable int id) {
        feedService.deleteById(id);
    }

}