package org.dodds.nfrapi.requirement;

import org.dodds.nfrapi.common.SecurityRules;
import org.dodds.nfrapi.users.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class RequirementSecurityRules implements SecurityRules {
    @Override
    public void configure(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers(HttpMethod.POST, "/requirements/**").hasRole(Role.ADMIN.name());
        registry.requestMatchers(HttpMethod.PUT, "/requirements/**").hasRole(Role.ADMIN.name());
        registry.requestMatchers(HttpMethod.DELETE, "/requirements/**").hasRole(Role.ADMIN.name());
    }
}
