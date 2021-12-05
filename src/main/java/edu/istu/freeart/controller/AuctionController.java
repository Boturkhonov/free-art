package edu.istu.freeart.controller;

import edu.istu.freeart.entity.Auction;
import edu.istu.freeart.repo.AuctionRepository;
import edu.istu.freeart.repo.ImageRepository;
import edu.istu.freeart.repo.UserRepository;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auctions")
public class AuctionController {

    private final UserRepository userRepository;

    private final AuctionRepository auctionRepository;

    private final ImageRepository imageRepository;

    private final Environment environment;

    public AuctionController(UserRepository userRepository,
            AuctionRepository auctionRepository,
            ImageRepository imageRepository,
            Environment environment) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.imageRepository = imageRepository;
        this.environment = environment;
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getAuction(@PathVariable Long id) {
        final Optional<Auction> optional = auctionRepository.findById(id);

        return optional.<ResponseEntity<Object>>map(auction -> ResponseEntity.status(HttpStatus.OK).body(auction))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found"));
    }

    @GetMapping
    public List<Auction> getAuctions() {
        List<Auction> auctions = auctionRepository.findAll();
        auctions = auctions.stream()
                .filter(auction -> auction.getBuyer() == null)
                .sorted(Comparator.comparing(Auction::getEndDate))
                .collect(Collectors.toList());
        auctions.forEach(auction -> {
            auction.setComments(null);
            auction.setBids(null);
        });
        return auctions;
    }

}
