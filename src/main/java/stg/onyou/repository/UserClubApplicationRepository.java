package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.UserClubApplication;

public interface UserClubApplicationRepository extends JpaRepository<UserClubApplication, Long> {
}
