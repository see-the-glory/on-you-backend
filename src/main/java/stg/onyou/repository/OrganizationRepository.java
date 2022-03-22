package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
}
