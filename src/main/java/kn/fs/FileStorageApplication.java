package kn.fs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan( basePackages = {"kn.fs.domain"} )
@EnableJpaRepositories("kn.fs.dao")
public class FileStorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileStorageApplication.class, args);
    }
}