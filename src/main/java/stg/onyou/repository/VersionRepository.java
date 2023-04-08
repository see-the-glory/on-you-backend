package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.Version;

@Repository
public interface VersionRepository extends JpaRepository<Version,Long> {
}
