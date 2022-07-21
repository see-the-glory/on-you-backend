package stg.onyou.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.NoRepositoryBean;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.QClub;
import stg.onyou.model.network.request.ClubSearchRequest;

import java.util.List;


@NoRepositoryBean
public class ClubRepositoryImpl extends QuerydslRepositorySupport implements ClubQRepository{

    public ClubRepositoryImpl() {
        super(Club.class);
    }

    @Override
    public Page<Club> findClubSearchList(Pageable page, ClubSearchRequest clubSearchRequest) {

        QClub club = QClub.club;
        List<Club> result = from(club)
                .where(
                        searchEq(clubSearchRequest)
                )
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .orderBy(clubSort(page))
                .fetch();

        long total = result.size();
        return new PageImpl<>(result, page, total);
    }

    private Predicate searchEq(ClubSearchRequest clubSearchRequest) {
        return null;
    }

    private OrderSpecifier<?> clubSort(Pageable page) {

        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        if (!page.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다

            PathBuilder orderByExpression = new PathBuilder(Club.class, "club");

            for (Sort.Order order : page.getSort()) {

                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
//                switch (order.getProperty()){
//                    case "created":
//                        return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
//                    case "memberNum":
//                        return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
//                    case "feedNum":
//                        return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
//                    case "likesNum":
//                        return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
//                }
            }
        }
        return null;
    }
}
