package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
