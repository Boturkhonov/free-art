package edu.istu.freeart.controller;

import edu.istu.freeart.entity.Auction;
import edu.istu.freeart.entity.Bid;
import edu.istu.freeart.entity.Image;
import edu.istu.freeart.entity.User;
import edu.istu.freeart.repo.AuctionRepository;
import edu.istu.freeart.repo.BidRepository;
import edu.istu.freeart.repo.ImageRepository;
import edu.istu.freeart.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auctions")
public class AuctionController {

    private final UserRepository userRepository;

    private final AuctionRepository auctionRepository;

    private final ImageRepository imageRepository;

    private final BidRepository bidRepository;

    public AuctionController(UserRepository userRepository,
            AuctionRepository auctionRepository,
            ImageRepository imageRepository,
            BidRepository bidRepository) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.imageRepository = imageRepository;
        this.bidRepository = bidRepository;
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getAuction(@PathVariable Long id) {
        final Optional<Auction> optional = auctionRepository.findById(id);
        if (!optional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        final Auction auction = optional.get();
        auction.getBids().sort((o1, o2) -> o2.getPrice() - o1.getPrice());
        auction.getComments().sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        return ResponseEntity.ok(auction);
    }

    @GetMapping
    public List<Auction> getAuctions() {
        List<Auction> auctions = auctionRepository.findAll();
        auctions = auctions.stream().filter(auction -> auction.getBuyer() == null).collect(Collectors.toList());
        auctions.forEach(auction -> {
            auction.setComments(null);
            auction.setBids(null);
        });
        auctions.sort(
                (o1, o2) -> Comparator.nullsLast(LocalDateTime::compareTo).compare(o1.getEndDate(), o2.getEndDate()));
        return auctions;
    }

    @PostMapping
    public ResponseEntity<?> newAuction(@RequestBody Map<String, Object> requestBody, Principal principal) {
        Long imageId = new Long((Integer) requestBody.get("imageId"));
        Optional<Image> optional = imageRepository.findById(imageId);
        Integer price = (Integer) requestBody.get("price");
        Optional<User> userOptional = userRepository.findByLogin(principal.getName());
        if (!optional.isPresent() || !userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        final Auction auction = new Auction();
        auction.setPrice(price);
        auction.setImage(optional.get());
        auction.setStartDate(LocalDateTime.now(ZoneOffset.UTC));
        auction.setSeller(userOptional.get());

        Auction saved = auctionRepository.save(auction);

        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setPrice(price);
        bid.setUser(userOptional.get());
        bid.setDate(LocalDateTime.now(ZoneOffset.UTC));
        bidRepository.save(bid);

        return ResponseEntity.ok(saved);
    }

}
