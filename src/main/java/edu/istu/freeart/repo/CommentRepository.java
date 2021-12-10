package edu.istu.freeart.repo;

import edu.istu.freeart.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Integer deleteByUserLoginAndId(@NotNull String userLogin, Long id);
}
