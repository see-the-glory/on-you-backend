package stg.onyou.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import stg.onyou.model.Role;
import stg.onyou.model.entity.QUser;
import stg.onyou.model.entity.QUserClub;
import stg.onyou.model.entity.UserClub;
import stg.onyou.model.entity.UserNotification;
import stg.onyou.model.network.response.ClubNotificationResponse;
import stg.onyou.model.network.response.QClubNotificationResponse;

import java.util.List;
import java.util.Optional;

import static stg.onyou.model.entity.QUser.user;
import static stg.onyou.model.entity.QUserClub.userClub;

@Repository
public class UserClubQRepositoryImpl extends QuerydslRepositorySupport implements UserClubQRepository{


    private final JPAQueryFactory queryFactory;

    @Autowired
    public UserClubQRepositoryImpl(JPAQueryFactory queryFactory) {
        super(UserClub.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<UserClub> findLatestManager(Long clubId) {
        QUserClub uc = QUserClub.userClub;
        return Optional.ofNullable(queryFactory
                .select(uc)
                .from(userClub)
                .where(userClub.club.id.eq(clubId), userClub.role.eq(Role.MANAGER))
                .orderBy(userClub.id.desc())
                .limit(1)
                .fetchOne());
    }

    @Override
    public Optional<UserClub> findLatestMember(Long clubId) {
        QUserClub uc = QUserClub.userClub;
        return Optional.ofNullable(queryFactory
                .select(uc)
                .from(userClub)
                .where(userClub.club.id.eq(clubId), userClub.role.eq(Role.MEMBER))
                .orderBy(userClub.id.desc())
                .limit(1)
                .fetchOne());
    }
}
