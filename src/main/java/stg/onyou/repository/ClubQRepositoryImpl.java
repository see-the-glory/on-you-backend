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
import stg.onyou.model.network.request.ClubCondition;
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
    public Page<ClubConditionResponse> findClubSearchList(Pageable page, ClubCondition clubCondition, String customCursor) {

        List<ClubConditionResponse> clubResult = findClubList(page, clubCondition, customCursor);

        clubResult.forEach(
                r -> {
                    r.setMembers(setMember(r));
                    r.setCategories(setCategory(r));
                }
        );

        long total = clubResult.size();
        return new PageImpl<>(clubResult, page, total);
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

    private List<ClubConditionResponse> findClubList(Pageable page, ClubCondition clubCondition, String customCursor) {

        String customSortType = getCustomSortType(page);
        StringTemplate stringTemplate = getCustomStringTemplate(customSortType);

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
                        club.created,
                        StringExpressions.lpad(stringTemplate, 20, '0')
                                .concat(StringExpressions.lpad(club.id.stringValue(), 10, '0'))

                ))
                .from(club)
                .leftJoin(club.organization, organization)
                .leftJoin(club.creator, user)
                .where(
                        customCursorCompare(page, clubCondition, customCursor)

                )
//                .orderBy(club.created.asc(), club.id.asc())
                .orderBy(clubSort(page, clubCondition))
                .limit(page.getPageSize())
                .fetch();
    }

    private BooleanExpression customCursorCompare(Pageable page, ClubCondition clubCondition, String customCursor){

        if (customCursor == null) { // 첫 페이지 조회를 위한 처리
            return null;
        }

        String customSortType = getCustomSortType(page);
        Order customDirection = getCustomDirection(clubCondition);
        StringTemplate stringTemplate = getCustomStringTemplate(customSortType);

        if( customDirection.equals(Order.ASC) ){
            return StringExpressions.lpad(stringTemplate, 20, '0')
                    .concat(StringExpressions.lpad(club.id.stringValue(), 10, '0'))
                    .gt(customCursor);
        } else {
            return StringExpressions.lpad(stringTemplate, 20, '0')
                    .concat(StringExpressions.lpad(club.id.stringValue(), 10, '0'))
                    .lt(customCursor);
        }

    }

    private Order getCustomDirection(ClubCondition clubCondition) {
        return clubCondition.getOrderBy().equals("ASC") ? Order.ASC : Order.DESC;
    }

    private StringTemplate getCustomStringTemplate(String customSortType) {

        switch(customSortType){
            case "created":
                return Expressions.stringTemplate(
                        "DATE_FORMAT({0}, {1})",
                        club.created,
                        ConstantImpl.create("%Y%m%d%H%i%s"));
            case "recruitNum":
                return Expressions.stringTemplate(""+club.recruitNumber);
            //case "FEED_NUM" :
            //    break;
            //case "LIKES_NUM" :
            //    break;
            default :
                return null;
        }
    }

    private String getCustomSortType(Pageable page) {

        String res="";
        for(Sort.Order order : page.getSort()){
            res = order.getProperty();
        }
        return res;
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

    private OrderSpecifier<?> clubSort(Pageable page, ClubCondition clubCondition) {

        //서비스에서 보내준 Pageable 객체에 정렬조건 값 체크
        if (!page.getSort().isEmpty()) {

            for (Sort.Order order : page.getSort()) {

                Order direction = getCustomDirection(clubCondition);

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
