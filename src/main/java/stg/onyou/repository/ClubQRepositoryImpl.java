package stg.onyou.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
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
import stg.onyou.model.ApplyStatus;
import stg.onyou.model.RecruitStatus;
import stg.onyou.model.entity.Club;
import stg.onyou.model.entity.QUser;
import stg.onyou.model.network.request.ClubSearchRequest;
import stg.onyou.model.network.response.*;

import java.time.LocalDateTime;
import java.util.List;

import static stg.onyou.model.entity.QClub.club;
import static stg.onyou.model.entity.QUser.user;
import static stg.onyou.model.entity.QUserClub.userClub;
import static stg.onyou.model.entity.QOrganization.organization;
import static stg.onyou.model.entity.QClubCategory.clubCategory;
import static stg.onyou.model.entity.QCategory.category;

public class ClubQRepositoryImpl extends QuerydslRepositorySupport implements ClubQRepository{

    @Autowired
    private final JPAQueryFactory queryFactory;

    public ClubQRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Club.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<ClubConditionResponse> findClubSearchList(String customCursor, Pageable page, ClubSearchRequest clubSearchRequest) {

        List<ClubConditionResponse> clubResult = findClubList(customCursor, page, clubSearchRequest);

        clubResult.forEach(
                r -> {
                    r.setMembers(setMember(r));
                    r.setCategories(setCategory(r));
                }
        );

        long total = clubResult.size();
        return new PageImpl<ClubConditionResponse>(clubResult, page, total);
    }

    private List<CategoryResponse> setCategory(ClubConditionResponse r) {
        return queryFactory
                .select(new QCategoryResponse(
                        category.id,
                        category.name,
                        category.description,
                        category.thumbnail,
                        clubCategory.order
                ))
                .from(clubCategory)
                .leftJoin(clubCategory.category, category)
                .where(clubCategory.club.id.eq(r.getId()))
                .fetch();
    }

    private List<UserResponse> setMember(ClubConditionResponse r) {
        return queryFactory
                .select(new QUserResponse(
                        user.id,
                        user.name,
                        user.birthday,
                        organization.name,
                        userClub.applyStatus,
                        user.sex,
                        user.account_email,
                        user.created
                ))
                .from(userClub)
                .leftJoin(userClub.user, user)
                .leftJoin(user.organization, organization)
                .where(userClub.club.id.eq(r.getId()))
                .fetch();
    }

    private List<ClubConditionResponse> findClubList(String customCursor, Pageable page, ClubSearchRequest clubSearchRequest) {
        return queryFactory
                .select(new QClubConditionResponse(
                        club.id,
                        club.name,
                        club.shortDesc,
                        club.longDesc,
                        organization.name,
                        club.maxNumber,
                        club.recruitNumber,
                        club.thumbnail,
                        club.recruitStatus,
                        user.name,
                        club.created
                ))
                .from(club)
                .leftJoin(club.organization, organization)
                .leftJoin(club.creator, user)
                .where(
                        customCursor(customCursor, page, clubSearchRequest)

                )
//                .orderBy(club.created.asc(), club.id.asc())
                .orderBy(clubSort(page, clubSearchRequest))
                .limit(page.getPageSize())
                .fetch();
    }

    private BooleanExpression customCursor(String customCursor, Pageable page, ClubSearchRequest clubSearchRequest){

        if (customCursor == null) { // 1. 첫 페이지 조회를 위한 처리
            return null;
        }

        StringTemplate stringTemplate = Expressions.stringTemplate("");

        String customSortType = "";
        Order customDirection = clubSearchRequest.getOrderBy().equals("ASC") ? Order.ASC : Order.DESC;
//
//        Sort.Direction customDirection = Sort.Direction.ASC;

        for(Sort.Order order : page.getSort()){
            customSortType = order.getProperty();
        }


        /* 2. Querydsl에서 MySQL의 함수를 사용하기 위해서 Expressions.stringTemplate()를 사용
              첫 번째 파라미터에 원하는 템플릿을 명시하고, {0}과 {1} 부분이 다음 파라미터로 치환 */
        switch(customSortType){
            case "created":
                stringTemplate = Expressions.stringTemplate(
                        "DATE_FORMAT({0}, {1})",
                        club.created,
                        ConstantImpl.create("%Y%m%d%H%i%s"));
                break;
            case "recruitNum":
//                stringTemplate = Expressions.stringTemplate("%d",club.recruitNumber);
                break;
            //case "FEED_NUM" :
            //    break;
            //case "LIKES_NUM" :
            //    break;
            default :
                System.out.println("default");
        }

        if( customDirection.equals(Order.ASC) ){
            return StringExpressions.lpad(stringTemplate, 20, '0')
                    .concat(StringExpressions.lpad(club.id.stringValue(), 10, '0'))
                    .gt(customCursor);
//            .gt("000000201908010925120000000005");
        } else {
            return StringExpressions.lpad(club.recruitNumber.stringValue(), 20, '0')
                    .concat(StringExpressions.lpad(club.id.stringValue(), 10, '0'))
                    .lt(customCursor);
        }

    }

    private BooleanExpression cursorId(Long cursorId){
        return cursorId == null ? null : club.id.lt(cursorId);
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

    private OrderSpecifier<?> clubSort(Pageable page, ClubSearchRequest clubSearchRequest) {

        //서비스에서 보내준 Pageable 객체에 정렬조건 null 값 체크
        if (!page.getSort().isEmpty()) {
            //정렬값이 들어 있으면 for 사용하여 값을 가져온다

            PathBuilder orderByExpression = new PathBuilder(Club.class, "club");

            for (Sort.Order order : page.getSort()) {

                // 서비스에서 넣어준 DESC or ASC 를 가져온다.
                Order direction = clubSearchRequest.getOrderBy().equals("ASC") ? Order.ASC : Order.DESC;

                switch (order.getProperty()){
                    case "created":
                        return new OrderSpecifier(direction, club.created);
                    case "recruitNum":
                        return new OrderSpecifier(direction, club.recruitNumber);
//                    case "feedNum":
//                        return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
//                    case "likesNum":
//                        return new OrderSpecifier(direction, orderByExpression.get(order.getProperty()));
                }
            }
        }
        return null;
    }
}
