package com.swivel.qpon.fileuploader.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * Resource server configuration
 */
@Slf4j
@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private final String resourceId;

    public ResourceServerConfiguration(@Value("${security.oauth2.resource.id}") String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(resourceId);
    }
}