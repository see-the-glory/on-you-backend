package stg.onyou.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Comment;
import stg.onyou.model.entity.CommentLikes;
import stg.onyou.model.entity.User;
import stg.onyou.model.network.request.CommentUpdateRequest;
import stg.onyou.repository.CommentLikesRepository;
import stg.onyou.repository.CommentRepository;
import stg.onyou.repository.FeedRepository;
import stg.onyou.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikesRepository commentLikesRepository;
    private final UserRepository userRepository;

    public void updateComment(Long id, CommentUpdateRequest updateComment) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.setContent(updateComment.getContent());
        comment.setUpdated(LocalDateTime.now());
    }

    public void likeComment(Long id, Long userId){
        Comment comment = this.findById(id);
        User user =  userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        CommentLikes commentLikes = CommentLikes.builder()
                .comment(comment)
                .user(user)
                .created(LocalDateTime.now())
                .build();

        commentLikesRepository.save(commentLikes);
    }

    public void deleteById(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.setDelYn('y');
    }

    public Comment findById(Long id){
        return commentRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
