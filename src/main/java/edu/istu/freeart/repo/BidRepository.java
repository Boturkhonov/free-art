package edu.istu.freeart.repo;

import edu.istu.freeart.entity.Auction;
import edu.istu.freeart.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findAllByAuction(@NotNull Auction auction);
    List<Bid> findAllByAuctionOrderByPrice(@NotNull Auction auction);
}
