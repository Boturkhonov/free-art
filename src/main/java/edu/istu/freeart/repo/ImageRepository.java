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
    Integer countAllByCreatorAndIsActivated(@NotNull User creator, @NotNull Boolean isActivated);
    List<Image> findAllByCreator(@NotNull User creator);
    List<Image> findAllByOwner(@NotNull User owner);
    List<Image> findAllByIsActivated(Boolean isActivated);
    Optional<Image> findByIdAndIsActivated(Long id, Boolean isActivated);
}
