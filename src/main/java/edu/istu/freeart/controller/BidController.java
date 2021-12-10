package edu.istu.freeart.controller;

import edu.istu.freeart.entity.Auction;
import edu.istu.freeart.entity.Bid;
import edu.istu.freeart.entity.User;
import edu.istu.freeart.repo.AuctionRepository;
import edu.istu.freeart.repo.BidRepository;
import edu.istu.freeart.repo.ImageRepository;
import edu.istu.freeart.repo.UserRepository;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@RestController
@RequestMapping("api/bids")
public class BidController {

    @Value("${edu.istu.free-art.auction-delay}")
    private Long auctionDelay;

    private final AuctionRepository auctionRepository;

    private final BidRepository bidRepository;

    private final UserRepository userRepository;

    private final ImageRepository imageRepository;

    public BidController(AuctionRepository auctionRepository,
            BidRepository bidRepository,
            UserRepository userRepository,
            ImageRepository imageRepository) {
        this.auctionRepository = auctionRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    @PostMapping
    public ResponseEntity<?> makeBid(@RequestBody Map<String, Object> request, Principal principal) {
        long auctionId = (int) request.get("auctionId");
        final Integer price = (Integer) request.get("price");
        final Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ObjectNotFoundException(auctionId, Auction.TYPE_NAME));
        final User user = userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

        final List<Bid> bids = bidRepository.findAllByAuctionOrderByPrice(auction);
        if (price <= bids.get(bids.size() - 1).getPrice() || user.getPoints() < price) {
            return ResponseEntity.badRequest().build();
        }
        final Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setPrice(price);
        bid.setUser(user);
        user.setPoints(user.getPoints() - price);
        bid.setDate(LocalDateTime.now(ZoneOffset.UTC));
        bidRepository.save(bid);

        auction.setEndDate(bid.getDate().plusSeconds(auctionDelay));
        auction.setPrice(price);
        auctionRepository.save(auction);

        if (auction.getBids().size() == 2) {
            createTask(auction);
        }
        return ResponseEntity.ok().build();
    }

    private void createTask(final Auction auction) {
        final Date date = Date.from(auction.getEndDate().toInstant(ZoneOffset.UTC));
        new Timer("Timer").schedule(new AuctionTask(auction), date);
    }

    private class AuctionTask extends TimerTask {

        private Auction auction;

        public AuctionTask(Auction auction) {
            this.auction = auction;
        }

        @Override
        @Transactional
        public void run() {
            auction = auctionRepository.findById(auction.getId())
                    .orElseThrow(() -> new ObjectNotFoundException(auction.getId(), Auction.TYPE_NAME));
            final List<Bid> bids = bidRepository.findAllByAuction(auction);
            bids.sort(Comparator.comparingInt(Bid::getPrice));
            for (int i = 1; i < bids.size(); i++) {
                final Bid bid = bids.get(i);
                final User user = bid.getUser();
                if (i == bids.size() - 1) {
                    final User seller = auction.getSeller();
                    seller.setPoints(seller.getPoints() + bid.getPrice());
                    auction.setBuyer(user);
                    auction.getImage().setOwner(user);

                } else {
                    user.setPoints(user.getPoints() + bid.getPrice());
                }
                userRepository.save(user);
            }
            auctionRepository.save(auction);
            System.err.println("Here");
        }
    }
}
