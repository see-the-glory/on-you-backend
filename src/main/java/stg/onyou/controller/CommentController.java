package stg.onyou.controller;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import stg.onyou.model.entity.Comment;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.request.CommentUpdateRequest;
import stg.onyou.model.network.response.CommentResponse;
import stg.onyou.service.CommentService;
import stg.onyou.service.FeedService;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"Comment API Controller"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final FeedService feedService;
    private final CommentService commentService;

    @GetMapping("/api/comments/{id}")
    public Header<List<CommentResponse>> selectCommentListByFeed(@PathVariable Long id) {
        Feed feed = feedService.findById(id);
        List<CommentResponse> resultList = feed.getComments().stream()
                .filter(comment -> comment.getDelYn() != 'y')
                .map(c -> new CommentResponse(c.getUser().getId(), c.getId(), c.getUser().getName(), c.getContent(), c.getCreated())).collect(Collectors.toList());
        return Header.OK(resultList);
    }

//    @PutMapping("/api/comments/{id}")
//    public Header<Object> updateComment(@PathVariable Long id,
//                                        @RequestBody CommentUpdateRequest request) {
//        commentService.updateComment(id, request);
//        return Header.OK();
//    }

    @DeleteMapping("/api/comments/{id}")
    public Header<Object> deleteComment(@PathVariable Long id) {
        commentService.deleteById(id);
        return Header.OK("Comment 삭제 완료");
    }
}
