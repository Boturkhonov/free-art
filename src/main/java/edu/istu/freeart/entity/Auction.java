package edu.istu.freeart.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "auction")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Auction {

    public static String TYPE_NAME = "Auction";

    public static class Property {

        public final static String ID = "id";

        public final static String START_DATE = "startDate";

        public final static String END_DATE = "endDate";

        public final static String PRICE = "price";

        public final static String IMAGE = "image";

        public final static String BUYER = "buyer";

        public final static String SELLER = "seller";
    }

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @NotNull
    @Column(name = "price")
    private Integer price;

    @NotNull
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @NotNull
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "seller_id")
    private User seller;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
    private List<Bid> bids;

    @OneToMany(mappedBy = "auction", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Auction auction = (Auction) o;
        return id.equals(auction.id)
                && startDate.equals(auction.startDate)
                && endDate.equals(auction.endDate)
                && price.equals(auction.price)
                && image.equals(auction.image)
                && Objects.equals(buyer, auction.buyer)
                && seller.equals(auction.seller);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate, price, image, buyer, seller);
    }
}
