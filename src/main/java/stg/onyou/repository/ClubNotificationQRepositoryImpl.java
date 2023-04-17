package stg.onyou.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.UserNotification;
import stg.onyou.model.network.response.ClubNotificationResponse;
import stg.onyou.model.network.response.QClubNotificationResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static stg.onyou.model.entity.QClubNotification.clubNotification;
import static stg.onyou.model.entity.QAction.action;
import static stg.onyou.model.entity.QUser.user;
import static stg.onyou.model.entity.QUserAction.userAction;

@Repository
public class ClubNotificationQRepositoryImpl extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public ClubNotificationQRepositoryImpl(JPAQueryFactory queryFactory) {
        super(UserNotification.class);
        this.queryFactory = queryFactory;
    }

    public List<ClubNotificationResponse> findClubNotificationList(Long clubId) {

        List<ClubNotificationResponse> notificationResponseList = queryFactory
                .select(new QClubNotificationResponse(
                        action.id,
                        action.actioner.id,
                        action.actionee.id,
                        action.actionType,
                        action.message,
                        action.isProcessDone,
                        Expressions.cases()
                                .when(userAction.action.id.isNull()).then(false)
                                .otherwise(true).as("isRead"),
                        action.processDone,
                        action.created
                ))
                .from(clubNotification)
                .innerJoin(clubNotification.action, action)
                .leftJoin(userAction)
                .on(userAction.action.id.eq(action.id))
                .where(
                        clubNotification.club.id.eq(clubId),
                        action.created.goe(LocalDateTime.now().minusDays(28))
                )
                .fetch();

        notificationResponseList.forEach(notification -> {

            String actioneeName = Optional.ofNullable(notification.getActioneeId())
                    .map( id -> queryFactory
                            .select(user.name)
                            .from(user)
                            .where(user.id.eq(id))
                            .fetchOne())
                    .orElse("");

            String actionerName = Optional.ofNullable(notification.getActionerId())
                    .map( id -> queryFactory
                            .select(user.name)
                            .from(user)
                            .where(user.id.eq(id))
                            .fetchOne())
                    .orElse("");

            notification.setActioneeName(actioneeName);
            notification.setActionerName(actionerName);
        });

        return notificationResponseList;
    }

}
