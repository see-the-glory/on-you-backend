package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.UserPreference;

public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

}
