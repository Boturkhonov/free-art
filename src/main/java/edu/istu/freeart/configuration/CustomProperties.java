package edu.istu.freeart.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "edu.istu.free-art")
@NoArgsConstructor
@Getter
@Setter
public class CustomProperties {

    private String uploadDirectory;

    private String host;

    private String avatarFolder;

    private String imageFolder;

    private String defaultAvatarName;

    private Long auctionDelay;

    private Integer defaultPoints;

}