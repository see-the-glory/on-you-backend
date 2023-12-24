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
import stg.onyou.model.network.response.BoardResponse;
import stg.onyou.model.network.response.QBoardResponse;
import stg.onyou.service.LikesService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static stg.onyou.model.entity.QBoard.board;
import static stg.onyou.model.entity.QFeed.feed;

@Repository
public class BoardQRepositoryImpl extends QuerydslRepositorySupport implements BoardQRepository{

    private final JPAQueryFactory queryFactory;
    @Autowired
    private final BoardRepository boardRepository;
    @Autowired
    private final LikesService likesService;

    @Autowired
    public BoardQRepositoryImpl(JPAQueryFactory queryFactory, BoardRepository boardRepository, LikesService likesService) {
        super(Club.class);
        this.queryFactory = queryFactory;
        this.boardRepository = boardRepository;
        this.likesService = likesService;
    }

    @Override
    public Page<BoardResponse> findBoardList(Pageable page, String customCursor, Long userId) {
        StringTemplate stringTemplate = getCustomStringTemplate();

        List<BoardResponse> boardResult = queryFactory
                .select(new QBoardResponse(
                        board.id,
                        board.user.id,
                        board.content,
                        board.created,
                        board.updated,
                        StringExpressions.lpad(stringTemplate, 20, '0').concat(StringExpressions.lpad(board.id.stringValue(), 10, '0'))
                ))
                .from(board)
                .where(
                        board.delYn.eq('N'),
                        board.reportCount.lt(1),
                        cursorCompare(page, customCursor)
                )
                .orderBy(new OrderSpecifier(Order.DESC, board.created))
                .limit(page.getPageSize())
                .fetch();

        fillAdditionalData(boardResult, userId);

        long total = boardResult.size();
        return new PageImpl<>(boardResult, page, total);
    }

    private BooleanExpression cursorCompare(Pageable page, String cursor){

        if (cursor == null) { // 첫 페이지 조회를 위한 처리
            return null;
        }
        StringTemplate stringTemplate = getCustomStringTemplate();

        return StringExpressions.lpad(stringTemplate, 20, '0')
                .concat(StringExpressions.lpad(board.id.stringValue(), 10, '0'))
                .lt(cursor);
    }
    private StringTemplate getCustomStringTemplate() {

        return Expressions.stringTemplate(
                "DATE_FORMAT({0}, {1})",
                board.created,
                ConstantImpl.create("%Y%m%d%H%i%s"));

    }

    private void fillAdditionalData(List<BoardResponse> feedResult, Long userId) {

        for(BoardResponse board : feedResult) {
            Board tempBoard = boardRepository.findById(board.getId()).orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

            Long likesCount = tempBoard.getLikes().stream().count();
            boolean likeYn = likesService.isLikesBoard(userId, tempBoard.getId());

            String imageUrl = tempBoard.getImageUrl();
            board.setImageUrl(imageUrl);
            board.setLikesCount(likesCount);
            board.setLikeYn(likeYn);

//            board.setCommentCount(tempBoard.getComments().stream().filter(comments -> comments.getDelYn()=='N').count());

        }
    }

}
