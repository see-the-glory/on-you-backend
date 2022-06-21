package stg.onyou.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.FeedHashtag;
import stg.onyou.model.entity.Hashtag;
import stg.onyou.repository.FeedHashtagRepository;
import stg.onyou.repository.FeedRepository;
import stg.onyou.repository.HashtagRepository;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final FeedRepository feedRepository;
    private final FeedHashtagRepository feedHashtagRepository;

    @Transactional
    public void addHashtagToFeed(Feed feed) {
        String[] splitString = feed.getContent().split(" ");
        for (String str : splitString) {
            if (str.charAt(0) == '#') {
                String hashtag = str.substring(1);
                Optional<Hashtag> findHashtag = hashtagRepository.findByHashtag(hashtag);
                if (findHashtag.isPresent()) {
                    FeedHashtag feedHashtag = FeedHashtag.builder()
                            .feed(feed)
                            .hashtag(findHashtag.orElseThrow(() -> new CustomException(ErrorCode.HASHTAG_NOT_FOUND)))
                            .build();
                    feedHashtagRepository.save(feedHashtag);

                } else {
                    Hashtag hashtagObj = Hashtag.builder()
                                    .hashtag(hashtag).build();
                    hashtagRepository.save(hashtagObj);
                    FeedHashtag feedHashtag = FeedHashtag.builder()
                            .hashtag(hashtagObj)
                            .feed(feed)
                            .build();
                    feedHashtagRepository.save(feedHashtag);
                }
            }
        }

    }

}
