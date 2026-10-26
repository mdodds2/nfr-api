package org.dodds.nfrapi.users;

import org.dodds.nfrapi.common.SecurityRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class UserSecurityRules implements SecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers(HttpMethod.POST, "/users").permitAll();
        registry.requestMatchers(HttpMethod.GET, "/users").hasRole(Role.ADMIN.name());
        registry.requestMatchers(HttpMethod.PUT, "/users/{id}").hasRole(Role.ADMIN.name());
        registry.requestMatchers(HttpMethod.DELETE, "/users/{id}").hasRole(Role.ADMIN.name());
        registry.requestMatchers("/users/me", "/users/me/**").authenticated();
        registry.requestMatchers("/users/{id}", "/users/{id}/**").hasRole(Role.ADMIN.name());
    }
}
