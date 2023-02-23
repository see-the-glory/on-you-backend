package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Suggestion;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
}
