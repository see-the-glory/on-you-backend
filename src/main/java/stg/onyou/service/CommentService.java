package stg.onyou.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Comment;
import stg.onyou.model.entity.CommentLikes;
import stg.onyou.model.entity.User;
import stg.onyou.model.entity.UserPreference;
import stg.onyou.model.enums.PreferType;
import stg.onyou.model.network.request.CommentUpdateRequest;
import stg.onyou.repository.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikesRepository commentLikesRepository;
    private final UserRepository userRepository;
    private final UserPreferenceRepository userPreferenceRepository;

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

        commentLikesRepository.findLikesByUserIdAndCommentId(userId, comment.getId())
                .ifPresentOrElse(
                        likes -> commentLikesRepository.deleteById(likes.getId()),
                        () -> {
                            commentLikesRepository.save(commentLikes);

                            UserPreference userPreference = UserPreference.builder()
                                    .user(user)
                                    .preferType(PreferType.COMMENT_LIKE)
                                    .preferUser(comment.getUser())
                                    .created(LocalDateTime.now())
                                    .build();

                            userPreferenceRepository.save(userPreference);
                        }
                );
    }

    public void deleteById(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.setDelYn('y');

        List<Comment> childComments = commentRepository.findByParentIdAndDelYn(comment.getId(), 'n');

        childComments.forEach(
                comments -> {
                    comments.setDelYn('y');
                }
        );
        commentRepository.saveAll(childComments);
        commentRepository.save(comment);
    }

    public Comment findById(Long id){
        return commentRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }
}
