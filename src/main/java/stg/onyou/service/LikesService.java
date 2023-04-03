package stg.onyou.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.enums.PreferType;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.FeedLikes;
import stg.onyou.model.entity.User;
import stg.onyou.model.entity.UserPreference;
import stg.onyou.repository.FeedRepository;
import stg.onyou.repository.FeedLikesRepository;
import stg.onyou.repository.UserPreferenceRepository;
import stg.onyou.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final FeedLikesRepository feedLikesRepository;
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;
    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    public void addLikes(Long userId, Long feedId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));

        FeedLikes like = FeedLikes.builder()
                .user(user)
                .feed(feed)
                .created(LocalDateTime.now())
                .build();

        feedLikesRepository.findLikesByUserIdAndFeedId(userId, feedId)
                .ifPresentOrElse(
                        likes -> feedLikesRepository.deleteById(likes.getId()),
                        () -> {
                            feedLikesRepository.save(like);

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

    // '좋아요' 유무
    public boolean isLikes(Long userId, Long feedId) {
        if(feedLikesRepository.findLikesByUserIdAndFeedId(userId, feedId).isPresent()){
            return true;
        } else {
            return false;
        }
    }
}
