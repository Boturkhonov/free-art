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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "user", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    public static class Property {

        public final static String ID = "id";

        public final static String LOGIN = "login";

        public final static String PASSWORD = "password";

        public final static String ABOUT = "about";

        public final static String POINTS = "points";

        public final static String ROLE = "role";

        public final static String AVATAR_URL = "avatarUrl";

        public final static String FOLLOWING = "following";

        public final static String FOLLOWER = "follower";
    }

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "login", nullable = false)
    private String login;

    @JsonIgnore
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "about", nullable = false)
    private String about;

    @NotNull
    @Column(name = "points", nullable = false)
    private Integer points;

    @NotNull
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Access(AccessType.PROPERTY)
    @Getter(AccessLevel.NONE)
    @Column(name = "avatar_url", nullable = false)
    private String avatarUrl;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "subscription", joinColumns = { @JoinColumn(name = "follower_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<User> following;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "subscription", joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "follower_id") })
    private Set<User> followers;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return id.equals(user.id)
                && login.equals(user.login)
                && password.equals(user.password)
                && about.equals(user.about)
                && points.equals(user.points)
                && role == user.role
                && avatarUrl.equals(user.avatarUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password, about, points, role, avatarUrl);
    }

    @Override
    public String toString() {
        return "User{"
                + "id="
                + id
                + ", login='"
                + login
                + '\''
                + ", password='"
                + password
                + '\''
                + ", about='"
                + about
                + '\''
                + ", points="
                + points
                + ", role="
                + role
                + ", avatarName='"
                + avatarUrl
                + '\''
                + '}';
    }

    public String getAvatarUrl() {
        return CustomConstants.AVATAR_FOLDER + avatarUrl;
    }
}
