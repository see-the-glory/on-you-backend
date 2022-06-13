package stg.onyou.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.Likes;
import stg.onyou.repository.FeedRepository;
import stg.onyou.repository.LikesRepository;
import stg.onyou.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    public void addLikes(Long userId, Long feedId) {
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));
        Likes like = Likes.builder()
                .user(userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)))
                .feed(feed)
                .onOff(true)
                .build();
        likesRepository.save(like);
    }

    // 이미 '좋아요'를 했는데, 한번 더 누르면 '좋아요' 취소
    public void reversLikes(Long userId, Long feedId) {
        Likes like = likesRepository.findLikesByFeedIdAndUserId(feedId, userId);
        like.setOnOff(!like.isOnOff());
        likesRepository.save(like);
    }
}
