package stg.onyou.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.data.repository.NoRepositoryBean;
import stg.onyou.model.RecruitStatus;
import stg.onyou.model.entity.Club;
import stg.onyou.model.network.request.ClubSearchRequest;
import stg.onyou.model.network.response.ClubConditionResponse;
import stg.onyou.model.network.response.QClubConditionResponse;

import stg.onyou.model.network.response.ClubResponse;
import stg.onyou.model.network.response.UserResponse;

import java.util.List;

import static stg.onyou.model.entity.QClub.club;
import static stg.onyou.model.entity.QUser.user;
import static stg.onyou.model.entity.QUserClub.userClub;

public class ClubQRepositoryImpl extends QuerydslRepositorySupport implements ClubQRepository{

    @Autowired
    private final JPAQueryFactory queryFactory;

    public ClubQRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Club.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<ClubConditionResponse> findClubSearchList(Pageable page, ClubSearchRequest clubSearchRequest) {


//        List<Club> result = from(club)
//                .where(
//                        eqRecruitStatus(clubSearchRequest.getRecruitStatus())
//                )
//                .offset(page.getOffset())
//                .limit(page.getPageSize())
////                .orderBy(clubSort(page))
//                .fetch();
//
//        long total = result.size();
//        return new PageImpl<>(result, page, total);

//        private List<UserResponse> members;
//        private int maxNumber;
//        private int recruitNumber;
//        private String thumbnail;
//        private RecruitStatus recruitStatus; //BEGIN, RECRUIT, CLOSED
//        private String creatorName;
//        private String category1Name;
//        private String category2Name;
        List<ClubConditionResponse> result = queryFactory
                .select(new QClubConditionResponse(
                        club.id,
                        club.name,
                        club.shortDesc,
                        club.longDesc
                ))
                .from(club)
                .leftJoin(club.userClubs, userClub)
                .fetch();


        long total = result.size();
        return new PageImpl<ClubConditionResponse>(result, page, total);
//        return new PageImpl<>(result, page, total);
    }

    private BooleanExpression eqRecruitStatus(RecruitStatus recruitStatus) {
        if(recruitStatus == null) {
            return null;
        }
        return club.recruitStatus.eq(recruitStatus);
    }

    private BooleanExpression betweenMemberMinMax(Integer minMemberNum, Integer maxMemberNum) {
        if(minMemberNum == null || maxMemberNum == null) {
            return null;
        }
        return club.maxNumber.between(minMemberNum, maxMemberNum);
    }

//    private OrderSpecifier<?> clubSort(Pageable page) {
//
//        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
//        if (!page.getSort().isEmpty()) {
//            //정렬값이 들어 있으면 for 사용하여 값을 가져온다
//
//            PathBuilder orderByExpression = new PathBuilder(Club.class, "club");
//
//            for (Sort.Order order : page.getSort()) {
//
//                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
//                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
//
//                return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
//                // 서비스에서 넣어준 정렬 조건을 스위치 케이스 문을 활용하여 셋팅하여 준다.
////                switch (order.getProperty()){
////                    case "created":
////                        return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
////                    case "memberNum":
////                        return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
////                    case "feedNum":
////                        return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
////                    case "likesNum":
////                        return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
////                }
//            }
//        }
//        return null;
//    }
}
