package stg.onyou.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import stg.onyou.model.entity.Content;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {
    Page<Content> findAll(Pageable pageable);
    List<Content> findAllByOrderByIdDesc(Pageable page);
    List<Content> findByIdLessThanOrderByIdDesc(Long id, Pageable page);
    Boolean existsByIdLessThan(Long id);
}
