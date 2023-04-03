package stg.onyou.controller;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Comment;
import stg.onyou.model.network.Header;
import stg.onyou.service.CommentService;
import stg.onyou.service.FeedService;
import stg.onyou.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Api(tags = {"Comment API Controller"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {
    private final FeedService feedService;
    private final CommentService commentService;

    private final UserService userService;

//    @PutMapping("/api/comments/{id}")
//    public Header<Object> updateComment(@PathVariable Long id,
//                                        @RequestBody CommentUpdateRequest request) {
//        commentService.updateComment(id, request);
//        return Header.OK();
//    }

    @DeleteMapping("/{id}")
    public Header<Object> deleteComment(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        Comment comment = commentService.findById(id);
        if (Objects.equals(comment.getUser().getId(), userId)) {
            commentService.deleteById(id);
            return Header.OK("Comment 삭제 완료");
        } else {
            throw new CustomException(ErrorCode.NO_AUTH_DELETE_COMMENT);
        }
    }

    @PostMapping("/{id}/likes")
    public Header<Object> likeComment(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        Long userId = userService.getUserId(httpServletRequest);
        commentService.likeComment(id, userId);
        return Header.OK("댓글 좋아요 완료");
    }
}
