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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final FeedHashtagRepository feedHashtagRepository;

    @Transactional
    public List<FeedHashtag> addHashtagToFeed(Feed feed) {
        String[] splitString = feed.getContent().split(" ");
        List<FeedHashtag> result = new ArrayList<>();
        for (String str : splitString) {
            // 빈 문자열인지 확인하고 처리
            if (!str.isEmpty() && str.charAt(0) == '#') {
                String hashtag = str.substring(1);
                Optional<Hashtag> findHashtag = hashtagRepository.findByHashtag(hashtag);
                if (findHashtag.isPresent()) {
                    FeedHashtag feedHashtag = FeedHashtag.builder()
                            .feed(feed)
                            .hashtag(findHashtag.orElseThrow(() -> new CustomException(ErrorCode.HASHTAG_NOT_FOUND)))
                            .build();
                    feedHashtagRepository.save(feedHashtag);
                    result.add(feedHashtag);
                } else {
                    Hashtag hashtagObj = Hashtag.builder()
                            .hashtag(hashtag).build();
                    hashtagRepository.save(hashtagObj);
                    FeedHashtag feedHashtag = FeedHashtag.builder()
                            .hashtag(hashtagObj)
                            .feed(feed)
                            .build();
                    feedHashtagRepository.save(feedHashtag);
                    result.add(feedHashtag);
                }
            }
        }
        return result;
    }


}
