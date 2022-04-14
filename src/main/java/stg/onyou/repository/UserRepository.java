package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByAccountEmail(String accountEmail);
    User findByName(String name);

    Optional<User> findBySocialId(String socialId);
}
