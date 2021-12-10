package edu.istu.freeart.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.istu.freeart.util.CustomConstants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "image", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Image {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "hash", nullable = false)
    private String hash;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_activated", nullable = false)
    private Boolean isActivated;

    @Access(AccessType.PROPERTY)
    @Setter(AccessLevel.NONE)
    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @Transient
    private Integer price;

    @Transient
    private Integer auctionCount;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "image_tag", joinColumns = @JoinColumn(name = "image_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    public void setUrl(String url) {
        String[] split = url.split("/");
        this.url = split[split.length - 1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Image image = (Image) o;
        return id.equals(image.id)
                && title.equals(image.title)
                && hash.equals(image.hash)
                && creator.equals(image.creator)
                && owner.equals(image.owner)
                && description.equals(image.description)
                && isActivated.equals(image.isActivated)
                && url.equals(image.url)
                && uploadDate.equals(image.uploadDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, hash, creator, owner, description, isActivated, url, uploadDate);
    }
}
