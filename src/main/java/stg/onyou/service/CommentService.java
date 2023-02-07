package stg.onyou.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Comment;
import stg.onyou.model.network.request.CommentUpdateRequest;
import stg.onyou.repository.CommentRepository;
import stg.onyou.repository.FeedRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    public void updateComment(Long id, CommentUpdateRequest updateComment) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.setContent(updateComment.getContent());
        comment.setUpdated(LocalDateTime.now());
    }

    @Transactional
    public void deleteById(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.setDelYn('y');
    }

    public Comment findById(Long id){
        return commentRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
