package edu.istu.freeart.controller;

import edu.istu.freeart.entity.Auction;
import edu.istu.freeart.entity.Image;
import edu.istu.freeart.entity.User;
import edu.istu.freeart.repo.AuctionRepository;
import edu.istu.freeart.repo.ImageRepository;
import edu.istu.freeart.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
public class DocumentController {

    private final UserRepository userRepository;

    private final ImageRepository imageRepository;

    private final AuctionRepository auctionRepository;

    public DocumentController(UserRepository userRepository,
            ImageRepository imageRepository,
            AuctionRepository auctionRepository) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.auctionRepository = auctionRepository;
    }

    @GetMapping("api/users/rating")
    public ResponseEntity<?> getUserRating() {
        final List<User> users = userRepository.findAll();
        users.forEach(user -> user.setCollectionPrice(userRepository.getCollectionCost(user)));
        users.sort((user1, user2) -> (user2.getCollectionPrice().compareTo(user1.getCollectionPrice())));
        return ResponseEntity.ok(users);
    }

    @GetMapping("api/auctions/transaction")
    public ResponseEntity<?> getAuctionTransactions() {
        final List<Auction> auctions = auctionRepository.findAllByBuyerNotNull();
        auctions.sort(Comparator.comparing(Auction::getEndDate));
        return ResponseEntity.ok(auctions);
    }

    @GetMapping("api/images/history")
    public ResponseEntity<?> getImageHistory() {
        final List<Image> images = imageRepository.findAll();
        images.forEach(image -> image.setAuctionCount(auctionRepository.countAllByImage(image)));
        images.sort(Comparator.comparingInt(Image::getAuctionCount));
        return ResponseEntity.ok(images);
    }

}
