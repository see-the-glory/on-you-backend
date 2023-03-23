package stg.onyou.repository;

import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import stg.onyou.exception.CustomException;
import stg.onyou.exception.ErrorCode;
import stg.onyou.model.enums.RecruitStatus;
import stg.onyou.model.entity.Category;
import stg.onyou.model.entity.Club;
//import stg.onyou.model.entity.QUser;
import stg.onyou.model.entity.User;
import stg.onyou.model.network.request.ClubCondition;
import stg.onyou.model.network.response.*;

import java.util.List;

import static stg.onyou.model.entity.QClub.club;
import static stg.onyou.model.entity.QUserClub.userClub;
import static stg.onyou.model.entity.QOrganization.organization;
import static stg.onyou.model.entity.QClubCategory.clubCategory;
import static stg.onyou.model.entity.QCategory.category;

public class ClubQRepositoryImpl extends QuerydslRepositorySupport implements ClubQRepository{


    private final JPAQueryFactory queryFactory;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    public ClubQRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Club.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<ClubConditionResponse> findClubSearchList(Pageable page, ClubCondition clubCondition, String cursor, Long userId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(
                        () -> new CustomException(ErrorCode.USER_NOT_FOUND)
                );

        List<ClubConditionResponse> clubResult = findClubList(page, clubCondition, cursor, currentUser);

        clubResult.forEach(
                club -> club.setCategories(setCategory(club))
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
                        clubCategory.sortOrder
                ))
                .from(clubCategory)
                .leftJoin(clubCategory.category, category)
                .where(clubCategory.club.id.eq(r.getId()))
                .fetch();
    }

    private List<ClubConditionResponse> findClubList(Pageable page, ClubCondition clubCondition, String cursor, User currentUser) {

        String customSortType = getCustomSortType(page); //
        StringTemplate stringTemplate = getCustomStringTemplate(customSortType);

        StringExpression cursorForEachRow  = StringExpressions.lpad(stringTemplate, 20, '0')
                .concat(StringExpressions.lpad(club.id.stringValue(), 10, '0'));

        return queryFactory
                .select(new QClubConditionResponse(
                        club.id,
                        club.name,
                        club.shortDesc,
                        organization.name,
                        club.maxNumber,
                        club.recruitNumber,
                        club.feedNumber,
                        club.clubLikesNumber,
                        club.thumbnail,
                        club.recruitStatus,
                        club.isApproveRequired,
                        club.created,
                        club.contactPhone,
                        cursorForEachRow
                ))
                .from(club)
                .leftJoin(club.organization, organization)
                .leftJoin(userClub).on(club.eq(userClub.club)) // 연관관계 없이 조인도 가능
                .where(
                        showRequestedCategory(clubCondition),
                        cursorCompare(page, clubCondition, cursor),
                        showMyClub(clubCondition, currentUser),
                        showRecruitingOnly(clubCondition),
                        showMemberBetween(clubCondition),
                        club.delYn.eq('N')
                )
                .groupBy(club)
                .orderBy(clubSort(page, clubCondition, cursorForEachRow))
                .limit(page.getPageSize())
                .fetch();
    }

    private BooleanExpression showRequestedCategory(ClubCondition clubCondition){
        if (clubCondition == null || clubCondition.getCategoryId()==0) {
            return null;
        }

        Category requestedCategory = categoryRepository.findById(clubCondition.getCategoryId())
                .orElseThrow(
                        () -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND)
                );

        return club.id.in(
                JPAExpressions.
                        select(club.id).from(clubCategory)
                        .innerJoin(clubCategory.club, club)
                        .where(clubCategory.category.eq(requestedCategory))
        );
    }



    private BooleanExpression showMyClub(ClubCondition clubCondition, User currentUser){
        if (clubCondition == null || clubCondition.getShowMy()==0) {
            return null;
        }
        return userClub.user.eq(currentUser);
    }

    private BooleanExpression showRecruitingOnly(ClubCondition clubCondition){
        if (clubCondition == null || clubCondition.getShowRecruitingOnly()==0) {
            return null;
        }
        return club.recruitStatus.eq(RecruitStatus.OPEN); //showRecruitingOnly == 1
    }

    private BooleanExpression showMemberBetween(ClubCondition clubCondition){
        if (clubCondition == null || clubCondition.getMin()==0 || clubCondition.getMax()==1000) {
            return null;
        }
        return club.recruitNumber.between(clubCondition.getMin(), clubCondition.getMax());
    }


    private BooleanExpression cursorCompare(Pageable page, ClubCondition clubCondition, String cursor){

        if (cursor == null) { // 첫 페이지 조회를 위한 처리
            return null;
        }

        String customSortType = getCustomSortType(page);
        Order customDirection = getCustomDirection(clubCondition);
        StringTemplate stringTemplate = getCustomStringTemplate(customSortType);

        if( customDirection.equals(Order.ASC) ){
            return StringExpressions.lpad(stringTemplate, 20, '0')
                    .concat(StringExpressions.lpad(club.id.stringValue(), 10, '0'))
                    .gt(cursor);
        } else {
            return StringExpressions.lpad(stringTemplate, 20, '0')
                    .concat(StringExpressions.lpad(club.id.stringValue(), 10, '0'))
                    .lt(cursor);
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
            case "feedNum" :
                return Expressions.stringTemplate(""+club.feedNumber);
            case "likesNum" :
                return Expressions.stringTemplate(""+club.clubLikesNumber);
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

    private OrderSpecifier<?> clubSort(Pageable page, ClubCondition clubCondition, StringExpression cursorForEachRow) {

        //서비스에서 보내준 Pageable 객체에 정렬조건 값 체크
        if (!page.getSort().isEmpty()) {

            for (Sort.Order order : page.getSort()) {

                Order direction = getCustomDirection(clubCondition);
                return new OrderSpecifier(direction, cursorForEachRow);
            }


        }
        return null;
    }
}
