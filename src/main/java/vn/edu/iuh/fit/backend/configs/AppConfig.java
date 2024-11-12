package vn.edu.iuh.fit.backend.configs;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"vn.edu.iuh.fit.backend.repositories"})
@EntityScan(basePackages = {"vn.edu.iuh.fit.backend.models"})
public class AppConfig {

}
