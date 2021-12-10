package edu.istu.freeart.repo;

import edu.istu.freeart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Long getCollectionCost(User user);
    Optional<User> findByLogin(@NotNull String login);
}
