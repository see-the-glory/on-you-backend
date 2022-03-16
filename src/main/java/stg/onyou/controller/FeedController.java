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
    private final String publicAccess = "public";

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/new")
    public String createForm() {
        return "feeds/createFeedForm";
    }


    @PostMapping("/new")
    public String create(FeedForm form) {
        Feed feed = new Feed();
        feed.setTitle(form.getTitle());
        feed.setContent(form.getContent());
        feed.setAccess(form.getAccess());
        feed.setCreated(LocalDateTime.now());
        feed.setGroupId(1);
        feed.setUserId(1);

        feedService.upload(feed);

        return "redirect:/";
    }

    @GetMapping("/all")
    public List<Feed> getPublicFeedList() {
        return feedService.findFeeds(publicAccess);
    }

    @GetMapping("/all/recent")
    public List<Feed> getPublicFeedListOrderByCreated() {
        return feedService.findFeedsOrderByCreated(publicAccess);
    }

    @GetMapping("")
    public List<Feed> searchFeedByTitleOrContent(@RequestParam String title, @RequestParam String content) {
        return feedService.findAllByTitleOrContent(title, content);
    }

    @GetMapping("/{id}")
    public Optional<Feed> getFeedInfo(@PathVariable int id) {
        return feedService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteFeedById(@PathVariable int id) {
        feedService.deleteById(id);
    }

}