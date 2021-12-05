package edu.istu.freeart.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "edu.istu")
@NoArgsConstructor
@Getter
@Setter
public class CustomProperties {

    private String uploadDirectory;

    private String host;

    private String avatarFolder;

    private String imageFolder;

}