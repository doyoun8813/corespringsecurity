package security.io.corespringsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import security.io.corespringsecurity.repository.ResourcesRepository;
import security.io.corespringsecurity.security.service.SecurityResourceService;

@Configuration
public class AppConfig {

    @Bean
    public SecurityResourceService securityResourceService(ResourcesRepository resourcesRepository) {
        SecurityResourceService securityResourceService = new SecurityResourceService(resourcesRepository);
        return securityResourceService;
    }
}
