package edu.istu.freeart.repo;

import edu.istu.freeart.entity.Auction;
import edu.istu.freeart.entity.Image;
import edu.istu.freeart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    Integer countAllBySellerAndBuyer(@NotNull User seller, User buyer);

    List<Auction> findAllByBuyerAndImageOrderByEndDate(User buyer, @NotNull Image image);

    List<Auction> findAllByBuyerNotNull();

    List<Auction> findAllBySellerAndBuyer(@NotNull User seller, User buyer);

    Integer countAllByImage(@NotNull Image image);
}
