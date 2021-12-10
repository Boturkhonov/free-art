package edu.istu.freeart.controller;

import edu.istu.freeart.entity.Auction;
import edu.istu.freeart.entity.Comment;
import edu.istu.freeart.entity.User;
import edu.istu.freeart.repo.AuctionRepository;
import edu.istu.freeart.repo.CommentRepository;
import edu.istu.freeart.repo.UserRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@RestController
@RequestMapping("api/comments")
public class CommentController {

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final AuctionRepository auctionRepository;

    public CommentController(UserRepository userRepository,
            CommentRepository commentRepository,
            AuctionRepository auctionRepository) {
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.auctionRepository = auctionRepository;
    }

    @PostMapping
    public ResponseEntity<?> postComment(@RequestBody Map<String, Object> request, Principal principal) {
        final User user = userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        final Long auctionId = new Long((Integer) request.get("auctionId"));
        final String content = (String) request.get("text");
        final Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ObjectNotFoundException(auctionId, Auction.TYPE_NAME));
        final Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuction(auction);
        comment.setDate(LocalDateTime.now(ZoneOffset.UTC));
        comment.setUser(user);

        return ResponseEntity.ok(commentRepository.save(comment));
    }

    @DeleteMapping("{id}")
    @Transactional
    public ResponseEntity<?> deleteComment(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(commentRepository.deleteByUserLoginAndId(principal.getName(), id));
    }
}
