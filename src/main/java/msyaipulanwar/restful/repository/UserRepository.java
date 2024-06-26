package msyaipulanwar.restful.repository;

import msyaipulanwar.restful.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findFirstByToken(String token);
}
