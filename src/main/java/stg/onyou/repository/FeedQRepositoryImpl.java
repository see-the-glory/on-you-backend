package stg.onyou.repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.AccessModifier;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.Feed;
import stg.onyou.model.entity.FeedHashtag;
import stg.onyou.model.entity.FeedImage;
import stg.onyou.model.network.response.FeedResponse;
import stg.onyou.model.network.response.QFeedResponse;
import stg.onyou.service.LikesService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static stg.onyou.model.entity.QFeed.feed;

@Repository
public class FeedQRepositoryImpl extends QuerydslRepositorySupport implements FeedQRepository{

    private final JPAQueryFactory queryFactory;
    @Autowired
    private final FeedRepository feedRepository;
    @Autowired
    private final LikesService likesService;
//    private final LikesRepository likesRepository;
//    private final FeedService feedService;

    @Autowired
    public FeedQRepositoryImpl(JPAQueryFactory queryFactory, FeedRepository feedRepository, LikesService likesService) {
        super(Club.class);
        this.queryFactory = queryFactory;
        this.feedRepository = feedRepository;
        this.likesService = likesService;
    }

    @Override
    public Page<FeedResponse> findFeedList(Pageable page, String cursor, Long userId) {

        StringTemplate stringTemplate = getCustomStringTemplate();

        List<FeedResponse> feedResult = queryFactory
                .select(new QFeedResponse(
                        feed.id,
                        feed.club.id,
                        feed.club.name,
                        feed.user.id,
                        feed.user.name,
                        feed.content,
                        feed.comments.size(),
                        feed.user.thumbnail,
                        feed.created,
                        feed.updated,
                        StringExpressions.lpad(stringTemplate, 20, '0')
                                .concat(StringExpressions.lpad(feed.id.stringValue(), 10, '0'))
                ))
                .from(feed)
                .where(
                        feed.delYn.eq('n'),
                        feed.reportCount.lt(5),
                        feed.access.eq(AccessModifier.valueOf("PUBLIC")),
                        cursorCompare(page, cursor)
                )
                .orderBy(new OrderSpecifier(Order.DESC, feed.created))
                .limit(page.getPageSize())
                .fetch();


        for(FeedResponse f : feedResult) {

            //임시로 쓸 feed 가져오기
            Feed tempFeed = feedRepository.findById(f.getId()).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));

            boolean likeYn = likesService.isLikes(userId, tempFeed.getId());
            int likesCount = tempFeed.getLikes().size();

            //hashtag 가져오기
            List<String> result = new ArrayList<>();
            List<FeedHashtag> feedHashtags = tempFeed.getFeedHashtags();
            for (FeedHashtag feedHashtag : feedHashtags) {
                result.add(feedHashtag.getHashtag().getHashtag());
            }
            List<String> hashtags = result;

            List<String> imageUrls = tempFeed.getFeedImages().stream().map(FeedImage::getUrl).collect(Collectors.toList());
            f.setHashtags(hashtags);
            f.setImageUrls(imageUrls);
            f.setLikeYn(likeYn);
            f.setLikesCount(likesCount);

        }

        long total = feedResult.size();
        return new PageImpl<>(feedResult, page, total);
    }

    @Override
    public Page<FeedResponse> findFeedListByClub(Pageable page, String cursor, Long userId, Long clubId) {

        StringTemplate stringTemplate = getCustomStringTemplate();

        List<FeedResponse> feedResult = queryFactory
                .select(new QFeedResponse(
                        feed.id,
                        feed.club.id,
                        feed.club.name,
                        feed.user.id,
                        feed.user.name,
                        feed.content,
                        feed.comments.size(),
                        feed.created,
                        feed.updated,
                        StringExpressions.lpad(stringTemplate, 20, '0')
                                .concat(StringExpressions.lpad(feed.id.stringValue(), 10, '0'))
                ))
                .from(feed)
                .where(
                        feed.delYn.eq('n'),
                        feed.reportCount.lt(5),
                        feed.access.eq(AccessModifier.valueOf("PUBLIC")),
                        feed.club.id.eq(clubId),
                        cursorCompare(page, cursor)
                )
                .orderBy(new OrderSpecifier(Order.DESC, feed.created))
                .limit(page.getPageSize())
                .fetch();


        for(FeedResponse f : feedResult) {

            //임시로 쓸 feed 가져오기
            Feed tempFeed = feedRepository.findById(f.getId()).orElseThrow(() -> new CustomException(ErrorCode.FEED_NOT_FOUND));

            boolean likeYn = likesService.isLikes(userId, tempFeed.getId());
            int likesCount = tempFeed.getLikes().size();

            //hashtag 가져오기
            List<String> result = new ArrayList<>();
            List<FeedHashtag> feedHashtags = tempFeed.getFeedHashtags();
            for (FeedHashtag feedHashtag : feedHashtags) {
                result.add(feedHashtag.getHashtag().getHashtag());
            }
            List<String> hashtags = result;

            List<String> imageUrls = tempFeed.getFeedImages().stream().map(FeedImage::getUrl).collect(Collectors.toList());
            f.setHashtags(hashtags);
            f.setImageUrls(imageUrls);
            f.setLikeYn(likeYn);
            f.setLikesCount(likesCount);

        }

        long total = feedResult.size();
        return new PageImpl<>(feedResult, page, total);
    }

    private StringTemplate getCustomStringTemplate() {

        return Expressions.stringTemplate(
                        "DATE_FORMAT({0}, {1})",
                        feed.created,
                        ConstantImpl.create("%Y%m%d%H%i%s"));

    }

    private BooleanExpression cursorCompare(Pageable page, String cursor){

        if (cursor == null) { // 첫 페이지 조회를 위한 처리
            return null;
        }
        StringTemplate stringTemplate = getCustomStringTemplate();

        return StringExpressions.lpad(stringTemplate, 20, '0')
                .concat(StringExpressions.lpad(feed.id.stringValue(), 10, '0'))
                .lt(cursor);
    }

}
