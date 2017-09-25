package org.springframework.sdr;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.sdr.services.SecurityService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class SpyingSecurityServiceConfig{

    @Bean
    public SecurityService securityService(){
        log.info("Creating Spy SecurityService");
        SecurityService securityService =  new SecurityService();
        return Mockito.spy(securityService);
    }
}