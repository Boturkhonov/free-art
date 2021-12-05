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
import java.time.LocalDateTime;
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

    @JsonIgnore
    @Column(name = "is_activated", nullable = false)
    private Boolean isActivated;

    @Access(AccessType.PROPERTY)
    @Getter(AccessLevel.NONE)
    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "image_tag", joinColumns = @JoinColumn(name = "image_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags;

    public String getUrl() {
        return CustomConstants.IMAGE_FOLDER + url;
    }
}
