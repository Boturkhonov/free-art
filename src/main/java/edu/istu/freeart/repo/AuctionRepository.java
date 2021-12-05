package edu.istu.freeart.repo;

import edu.istu.freeart.entity.Auction;
import edu.istu.freeart.entity.Image;
import edu.istu.freeart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
    Integer countAllBySellerAndBuyer(@NotNull User seller, User buyer);
    List<Auction> findAllByBuyerAndImage(User buyer, @NotNull Image image);
    List<Auction> findAllBySeller(User seller);
}
