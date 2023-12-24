package stg.onyou.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.*;
import stg.onyou.model.enums.PreferType;
import stg.onyou.repository.*;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final FeedLikesRepository feedLikesRepository;
    private final BoardLikesRepository boardLikesRepository;
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    public void addLikesFeed(Long userId, Long feedId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));

        FeedLikes like = FeedLikes.builder()
                .user(user)
                .feed(feed)
                .created(LocalDateTime.now())
                .build();

        feedLikesRepository.findLikesByUserIdAndFeedId(userId, feedId)
                .ifPresentOrElse(
                        likes -> feedLikesRepository.deleteById(likes.getId()), // if present
                        () -> {  //if not present

                            try {
                                feedLikesRepository.save(like);
                            } catch (DataIntegrityViolationException ex) {
                                throw new CustomException(ErrorCode.DUPLICATE_LIKE);
                            }

                            UserPreference userPreference = UserPreference.builder()
                                    .user(user)
                                    .preferType(PreferType.FEED_LIKE)
                                    .preferUser(feed.getUser())
                                    .preferClub(feed.getClub())
                                    .created(LocalDateTime.now())
                                    .build();

                            userPreferenceRepository.save(userPreference);
                        }
                );
    }

    public void addLikesBoard(Long userId, Long boardId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        BoardLikes like = BoardLikes.builder()
                .user(user)
                .board(board)
                .created(LocalDateTime.now())
                .build();

        boardLikesRepository.findLikesByUserIdAndBoardId(userId, boardId)
                .ifPresentOrElse(
                        likes -> boardLikesRepository.deleteById(likes.getId()), // if present
                        () -> {  //if not present

                            try {
                                boardLikesRepository.save(like);
                            } catch (DataIntegrityViolationException ex) {
                                throw new CustomException(ErrorCode.DUPLICATE_LIKE);
                            }
                        }
                );
    }

    // '좋아요' 유무
    public boolean isLikesFeed(Long userId, Long feedId) {
        if(feedLikesRepository.findLikesByUserIdAndFeedId(userId, feedId).isPresent()){
            return true;
        } else {
            return false;
        }
    }

    // '좋아요' 유무
    public boolean isLikesBoard(Long userId, Long feedId) {
        if(boardLikesRepository.findLikesByUserIdAndBoardId(userId, feedId).isPresent()){
            return true;
        } else {
            return false;
        }
    }
}
