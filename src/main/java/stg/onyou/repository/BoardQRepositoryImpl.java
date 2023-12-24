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
import stg.onyou.model.entity.*;
import stg.onyou.service.LikesService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static stg.onyou.model.entity.QUserBlock.userBlock;

@Repository
public class BoardQRepositoryImpl extends QuerydslRepositorySupport implements BoardQRepository{

    private final JPAQueryFactory queryFactory;

    @Autowired
    public BoardQRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Club.class);
        this.queryFactory = queryFactory;
    }

//    @Override
//    public Page<BoardResponse> findBoardList(Pageable page, String customCursor, Long userId) {
//        StringTemplate stringTemplate = getCustomStringTemplate();
//
//        List<BoardResponse> boardResult = queryFactory
//                .select(new QBoardResponse(
//                        board.id,
//                        board.user.id,
//                        board.content,
//                        board.created,
//                        board.updated,
//                        StringExpressions.lpad(stringTemplate, 20, '0').concat(StringExpressions.lpad(board.id.stringValue(), 10, '0'))
//                ))
//                .from(board)
//                .where(
//                        board.delYn.eq('N'),
//                        board.reportCount.lt(3),
//                        cursorCompare(page, cursor)
//                )
//                .orderBy(new OrderSpecifier(Order.DESC, board.created))
//                .limit(page.getPageSize())
//                .fetch();
//
//        fillAdditionalData(boardResult, userId);
//
//        long total = boardResult.size();
//        return new PageImpl<>(boardResult, page, total);
//    }

//    private StringTemplate getCustomStringTemplate() {
//
//        return Expressions.stringTemplate(
//                "DATE_FORMAT({0}, {1})",
//                board.created,
//                ConstantImpl.create("%Y%m%d%H%i%s"));
//
//    }

//    private void fillAdditionalData(List<BoardResponse> feedResult, Long userId) {
//
//        for(BoardResponse f : feedResult) {
//            Board tempBoard = feedRepository.findById(f.getId()).orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
//
//            Long likesCount = tempBoard.getLikes().stream().count();
//            boolean likeYn = likesService.isLikes(userId, tempBoard.getId());
//
//            String imageUrls = tempBoard.getImageUrl();
//            f.setImageUrl(imageUrls);
//            f.setLikesCount(likesCount);
//            f.setLikeYn(likeYn);
//
//            f.setCommentCount(tempBoard.getComments().stream().filter(comments -> comments.getDelYn()=='N').count());
//
//        }
//    }

}
