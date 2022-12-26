package stg.onyou.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.UserNotification;
import stg.onyou.model.network.response.ClubNotificationResponse;
import stg.onyou.model.network.response.QClubNotificationResponse;
import java.util.List;
import java.util.Optional;

import static stg.onyou.model.entity.QClubNotification.clubNotification;
import static stg.onyou.model.entity.QAction.action;
import static stg.onyou.model.entity.QUser.user;

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
                        action.applyMessage,
                        action.isProcessDone
                ))
                .from(clubNotification)
                .innerJoin(clubNotification.action, action)
                .where(clubNotification.club.id.eq(clubId))
                .fetch();

        for(ClubNotificationResponse notification : notificationResponseList){

            String actioneeName = "";
            if(notification.getActioneeId()!=null){
                actioneeName = queryFactory.select(user.name).from(user).where(user.id.eq(notification.getActioneeId()))
                        .fetchOne();
            }
            notification.setActioneeName(actioneeName);

            String actionerName = queryFactory.select(user.name).from(user).where(user.id.eq(notification.getActionerId()))
                    .fetchOne();
            notification.setActionerName(actionerName);
        }

        return notificationResponseList;
    }

}
