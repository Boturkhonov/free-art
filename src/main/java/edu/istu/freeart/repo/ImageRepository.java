package edu.istu.freeart.repo;

import edu.istu.freeart.entity.Image;
import edu.istu.freeart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Integer countAllByOwnerAndIsActivated(@NotNull User owner, @NotNull Boolean isActivated);
    List<Image> findAllByCreatorAndIsActivated(User creator, Boolean isActivated);
    List<Image> findAllByOwnerAndIsActivated(User owner, Boolean isActivated);
    List<Image> findAllByIsActivated(Boolean isActivated);
    Optional<Image> findByIdAndIsActivated(Long id, Boolean isActivated);
    Optional<Image> findByHash(String hash);
}
