package stg.onyou.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import stg.onyou.model.entity.Feed;
import stg.onyou.service.FeedService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class FeedController {

    private final FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/feeds/new")
    public String createForm() {
        return "feeds/createFeedForm";
    }


    @PostMapping("/feeds/new")
    public String create(FeedForm form) {
        Feed feed = new Feed();
        feed.setTitle(form.getTitle());
        feed.setContent(form.getContent());
        feed.setAccess(form.getAccess());
        feed.setCreated(LocalDateTime.now());

        feed.setGroupId(1L);
        feed.setUserId(1L);

        feedService.upload(feed);

        return "redirect:/";
    }

    @GetMapping("/feeds")
    @ResponseBody
    public List<Feed> list() {
        List<Feed> feeds = feedService.findFeeds();
        return feeds;
    }
}