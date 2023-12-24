package stg.onyou.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.enums.*;
import stg.onyou.model.entity.*;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.MessageMetaData;
import stg.onyou.model.network.request.*;
import stg.onyou.model.network.response.*;
import stg.onyou.repository.*;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ComponentScan(basePackages={"stg.onyou.repository"})
public class BoardService {

    @Autowired
    private final BoardRepository boardRepository;
    @Autowired
    private final BoardQRepositoryImpl boardQRepositoryImpl;
    @Autowired
    private final ClubRepository clubRepository;
    @Autowired
    private final FeedImageRepository feedImageRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final UserClubRepository userClubRepository;
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final ActionRepository actionRepository;
    @Autowired
    private final ClubNotificationRepository clubNotificationRepository;
    @Autowired
    private final UserNotificationRepository userNotificationRepository;
    @Autowired
    private final FirebaseCloudMessageService firebaseCloudMessageService;
    @Autowired
    private final UserPreferenceRepository userPreferenceRepository;
    @Autowired
    private final LikesService likesService;
    @Autowired
    private final CommentLikesRepository commentLikesRepository;

    /**
     * 특정 feed 수정
     */
    public void updateBoard(Long id, BoardUpdateRequest boardUpdateRequest) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        board.setContent(boardUpdateRequest.getContent());
        board.setUpdated(LocalDateTime.now());

        boardRepository.save(board);
    }

//    public void createLikeFeed(Long userId, Long feedId) {
//        Feed feed = feedRepository.findOne(feedId);
//        Optional<User> user = userRepository.findById(userId);
//
//        Likes like = Likes.builder()
//                .feed(feed)
//                .user(user.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)))
//                .build();
//        likesRepository.save(like);
//    }

//    public void createComment(Long userId, Long feedId, CommentCreateRequest commentCreateRequest) {
//        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
//        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
//
//        Comment parentComment = Optional.ofNullable(commentCreateRequest.getParentId())
//                .flatMap( parentId -> commentRepository.findById(parentId))
//                .orElseGet(() -> null);
//
//        Comment comment = Comment.builder()
//                .feed(feed)
//                .user(user)
//                .content(commentCreateRequest.getContent())
//                .created(LocalDateTime.now())
//                .updated(LocalDateTime.now())
//                .delYn('n')
//                .parent(Optional.ofNullable(parentComment).orElse(null))
//                .build();
//
//        // parent가 있을 경우 Parent comment를 작성한 사람에 대해서 prefer이 있는 것으로 간주,
//        // 그리고 대댓글 단 대상자에게 push, noti 주기
//        if(parentComment != null) {     // 답글인 경우.
//
//            User recipient = parentComment.getUser();
//
//            if (!recipient.equals(user)) {
//
//                UserPreference userPreference = UserPreference.builder()
//                        .user(user)
//                        .preferType(PreferType.COMMENT_REPLY)
//                        .preferUser(parentComment.getUser())
//                        .created(LocalDateTime.now())
//                        .build();
//
//                userPreferenceRepository.save(userPreference);
//
//                Action action = Action.builder()
//                        .actionFeed(feed)
//                        .actionType(ActionType.COMMENT_REPLY)
//                        .actioner(user)
//                        .created(LocalDateTime.now())
//                        .build();
//
//                actionRepository.save(action);
//
//
//                UserNotification userNotification = UserNotification.builder()
//                        .action(action)
//                        .recipient(recipient)
//                        .created(LocalDateTime.now())
//                        .build();
//
//                userNotificationRepository.save(userNotification);
//
//                try {
//                    if (recipient.getUserPushAlarm() == 'Y' && recipient.getTargetToken() != null) {
//
//                        MessageMetaData data = MessageMetaData.builder()
//                                .type(ActionType.COMMENT_REPLY)
//                                .actionId(action != null ? action.getId() : null)
//                                .feedId(feed != null ? feed.getId() : null)
//                                .commentId(comment != null ? comment.getId() : null)
//                                .build();
//
//
//                        Message fcmMessage = firebaseCloudMessageService.makeMessage(
//                                recipient.getTargetToken(),
//                                "새로운 답글",
//                                user.getName() + "님의 답글이 달렸습니다.",
//                                data);
//
//                        fcm.send(fcmMessage);
//                    }
//                } catch (FirebaseMessagingException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {    // 답글아닌 댓글인 경우
//            if( !feed.getUser().equals(user)) {
//                //Action Builder : FEED_COMMENT에 대한 Action 저장
//                Action action = Action.builder()
//                        .actionFeed(feed)
//                        .actionType(ActionType.FEED_COMMENT)
//                        .actioner(user)
//                        .created(LocalDateTime.now())
//                        .build();
//
//                actionRepository.save(action);
//
//                UserNotification userNotification = UserNotification.builder()
//                        .action(action)
//                        .recipient(feed.getUser())
//                        .created(LocalDateTime.now())
//                        .build();
//
//                userNotificationRepository.save(userNotification);
//
//                UserPreference userPreference = UserPreference.builder()
//                        .user(user)
//                        .preferType(PreferType.FEED_COMMENT)
//                        .preferUser(feed.getUser())
//                        .created(LocalDateTime.now())
//                        .build();
//
//                userPreferenceRepository.save(userPreference);
//
//                try {
//                    if (feed.getUser().getUserPushAlarm() == 'Y' && feed.getUser().getTargetToken() != null) {
//
//                        MessageMetaData data = MessageMetaData.builder()
//                                .type(ActionType.FEED_COMMENT)
//                                .actionId(action != null ? action.getId() : null)
//                                .feedId(feed != null ? feed.getId() : null)
//                                .commentId(comment != null ? comment.getId() : null)
//                                .build();
//
//                        Message fcmMessage = firebaseCloudMessageService.makeMessage(
//                                feed.getUser().getTargetToken(),
//                                "새로운 댓글",
//                                user.getName() + "님의 댓글이 달렸습니다.",
//                                data);
//
//                        fcm.send(fcmMessage);
//                    }
//                } catch (FirebaseMessagingException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//        commentRepository.save(comment);
//
//        // mention이 있을 경우 mention된 user에게 알림/푸시
//        Optional.ofNullable(commentCreateRequest.getMentionUserList())
//                .orElse(Collections.emptyList()).stream()
//                .map(uid -> userRepository.findById(uid)
//                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)))
//                .forEach(mentionedUser -> notifyMentionUser(mentionedUser, user, null, comment));
//
//    }

//    public List<CommentResponse> getComments(Long feedId, Long userId) {
//
//        List<Comment> comments = commentRepository.findByParentIdIsNullAndFeedIdOrderByCreatedDesc(feedId).stream()
//                .filter(comment -> comment.getDelYn() == 'n')
//                .collect(Collectors.toList());
//
//        List<CommentResponse> commentResponses = new ArrayList<>();
//
//        for (Comment comment : comments) {
//            CommentResponse commentResponse =  new CommentResponse();
//            commentResponse.setUserId(comment.getUser().getId());
//            commentResponse.setCommentId(comment.getId());
//            commentResponse.setThumbnail(comment.getUser().getThumbnail());
//            commentResponse.setUserName(comment.getUser().getName());
//            commentResponse.setContent(comment.getContent());
//            commentResponse.setCreated(comment.getCreated());
//            commentResponse.setLikeCount(
//                    comment.getLikes().size()
//            );
//            commentResponse.setLikeYn(isLikesComment(userId, comment.getId()));
//
//            List<LikeUserResponse> likeUserResponseList =
//                    comment.getLikes().stream()
//                            .map(like -> new LikeUserResponse(like.getUser().getId(), like.getUser().getName(),like.getUser().getThumbnail(), like.getCreated()))
//                            .collect(Collectors.toList());
//
//            commentResponse.setLikeUserResponseList(likeUserResponseList);
//
//            List<CommentResponse> replyResponses = new ArrayList<>();
//
//            List<Comment> replies = new ArrayList<>();
//            if(comment.getParent()==null){
//                replies = commentRepository.findByParentIdOrderByCreatedDesc(comment.getId())
//                        .stream()
//                        .filter(reply -> reply.getDelYn() == 'n')
//                        .collect(Collectors.toList());
//            }
//
//            for (Comment reply : replies) {
//
//                CommentResponse replyResponse = CommentResponse.builder()
//                        .userId(reply.getUser().getId())
//                        .commentId(reply.getId())
//                        .thumbnail(reply.getUser().getThumbnail())
//                        .userName(reply.getUser().getName())
//                        .content(reply.getContent())
//                        .likeCount(reply.getLikes().size())
//                        .likeYn(isLikesComment(userId, reply.getId()))
//                        .likeUserResponseList(
//                                reply.getLikes().stream()
//                                        .map(like -> new LikeUserResponse(like.getUser().getId(), like.getUser().getName(), like.getUser().getThumbnail(), like.getCreated()))
//                                        .collect(Collectors.toList())
//                        )
//                        .created(reply.getCreated())
//                        .build();
//
//                replyResponses.add(replyResponse);
//            }
//            commentResponse.setReplies(replyResponses);
//            commentResponses.add(commentResponse);
//        }
//        return commentResponses;
//    }

//    public int getLikesCount(Long boardId) {
//        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
//        return board.getLikes().size();
//    }

    private boolean isLikesComment(Long userId, Long commentId) {
        if(commentLikesRepository.findLikesByUserIdAndCommentId(userId, commentId).isPresent()){
            return true;
        } else {
            return false;
        }
    }
//    public List<LikeUserResponse> getCommentLikeUserList(Long id) {
//
//        Comment comment = commentRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
//
//        List<LikeUserResponse> likeUserResponseList =
//                comment.getLikes().stream()
//                        .map(like -> new LikeUserResponse(like.getUser().getId(), like.getUser().getName(), like.getUser().getThumbnail(), like.getCreated()))
//                        .collect(Collectors.toList());
//
//        return likeUserResponseList;
//    }

//    public BoardPageResponse getMyBoardList(Pageable page, String cursor, Long userId) {
//
//        Page<BoardResponse> foundBoardList = boardQRepositoryImpl.findMyBoardList(page, cursor, userId);
//
//        BoardPageResponse response = BoardPageResponse.builder()
//                .hasData(foundBoardList.getTotalElements()!=0?true:false)
//                .responses(foundBoardList)
//                .build();
//
//        return response;
//    }

    public BoardPageResponse selectBoardList(Pageable page, String cursor, Long userId) {

        Page<BoardResponse> foundBoardList = boardQRepositoryImpl.findBoardList(page, cursor, userId);

        BoardPageResponse response = BoardPageResponse.builder()
                .hasData(foundBoardList.getTotalElements()!=0?true:false)
                .responses(foundBoardList)
                .build();

        return response;
    }

    public void createBoard(BoardCreateRequest request, Long userId, String url) {

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        Board board = Board.builder()
                .user(user)
                .imageUrl(url)
                .content(request.getContent())
                .category(request.getCategory())
                .delYn('N')
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();

        boardRepository.save(board);

    }

    public void deleteBoard(Long boardId, Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        Board board  = boardRepository.findById(boardId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.BOARD_NOT_FOUND)
                );

        if ( board.getUser().getId() != userId ){
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }

        board.setDelYn('Y');
        boardRepository.save(board);
    }

}