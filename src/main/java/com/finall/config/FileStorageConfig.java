package com.finall.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ConfigurationProperties(prefix = "file")
@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class FileStorageConfig {

    @Value("${file.generate-link.expire-hours}")
    private Long generateLinkExpireHours;

    @Value("${file.generate-link.length}")
    private Long generateLinkLength;

    private String uploadDirectory;
    private String tempDirectory;

    public static Path getPathByDirectory(String directory) {
        Path storeLocation = Paths.get(directory);
        try {
            if (!storeLocation.toFile().exists()) {
                Files.createDirectories(storeLocation);
            }
        } catch (IOException e) {
            log.error("Could not create the directory", e);
        }
        return storeLocation;
    }

    @Bean(name = "uploadPath")
    Path getUploadPath() {
        return getPathByDirectory(this.uploadDirectory);
    }

    @Bean(name = "tempPath")
    Path getTempPath() {
        return getPathByDirectory(this.tempDirectory);
    }

}
