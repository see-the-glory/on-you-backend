package stg.onyou.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import stg.onyou.model.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByName(String name);
    boolean existsUserByEmail(String email);
    Optional<User> findByNameAndPhoneNumber(String username, String phoneNumber);
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndNameAndPhoneNumberAndBirthday(String email, String username, String phoneNumber, String birthday);
}
