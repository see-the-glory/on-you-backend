package stg.onyou.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.UserNotification;
import stg.onyou.model.network.response.QUserNotificationResponse;
import stg.onyou.model.network.response.UserNotificationResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static stg.onyou.model.entity.QUser.user;
import static stg.onyou.model.entity.QClub.club;
import static stg.onyou.model.entity.QUserAction.userAction;
import static stg.onyou.model.entity.QUserNotification.userNotification;
import static stg.onyou.model.entity.QAction.action;

@Repository
public class UserNotificationQRepositoryImpl extends QuerydslRepositorySupport implements UserNotificationQRepository{

    private final JPAQueryFactory queryFactory;
    @Autowired
    public UserNotificationQRepositoryImpl(JPAQueryFactory queryFactory) {
        super(UserNotification.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public List<UserNotificationResponse> findUserNotificationList(Long userId) {

        List<UserNotificationResponse> notificationResponseList = queryFactory.select(
                new QUserNotificationResponse(
                        action.id,
                        action.actioner.id,
                        action.actionee.id,
                        action.actionClub.id,
                        action.actionFeed.id,
                        action.actionComment.id,
                        action.actionType,
                        action.message,
                        action.isProcessDone,
                        Expressions.cases()
                                .when(userAction.action.id.isNull()).then(false)
                                .otherwise(true).as("isRead"),
                        action.created
                )
        )
                .from(userNotification)
                .innerJoin(userNotification.action, action)
                .leftJoin(userAction)
                .on(userAction.action.id.eq(action.id))
                .where(
                        userNotification.recipient.id.eq(userId),
                        action.created.goe(LocalDateTime.now().minusDays(7))
                )
                .fetch();

        notificationResponseList.forEach(notification -> {
            String actioneeName = Optional.ofNullable(notification.getActioneeId())
                    .map(id -> queryFactory.select(user.name)
                            .from(user)
                            .where(user.id.eq(id))
                            .fetchOne())
                    .orElse("");

            String actionerName = Optional.ofNullable(notification.getActionerId())
                    .map(id -> queryFactory.select(user.name)
                            .from(user)
                            .where(user.id.eq(id))
                            .fetchOne())
                    .orElse("");

            String actionClubName = Optional.ofNullable(notification.getActionClubId())
                    .map(id -> queryFactory.select(club.name)
                            .from(club)
                            .where(club.id.eq(id))
                            .fetchOne())
                    .orElse("");

            notification.setActioneeName(actioneeName);
            notification.setActionerName(actionerName);
            notification.setActionClubName(actionClubName);
        });


        return notificationResponseList;
    }

}
