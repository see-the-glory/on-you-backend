package stg.onyou.controller;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.CommentResponse;
import stg.onyou.service.FeedService;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"Comment API Controller"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final FeedService feedService;

    @GetMapping("/api/comments/{id}")
    public Header<List<CommentResponse>> selectCommentListByFeed(@PathVariable Long id) {
        Feed feed = feedService.findById(id);
        List<CommentResponse> resultList = feed.getComments().stream()
                .map(c -> new CommentResponse(c.getUser().getName(), c.getContent(), c.getCreated(), c.getUpdated())).collect(Collectors.toList());
        return Header.OK(resultList);
    }
}
