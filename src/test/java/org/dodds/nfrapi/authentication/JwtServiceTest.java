package org.dodds.nfrapi.authentication;

import io.jsonwebtoken.Jwts;
import org.dodds.nfrapi.org.OrgMembership;
import org.dodds.nfrapi.org.OrgRole;
import org.dodds.nfrapi.org.Organization;
import org.dodds.nfrapi.users.Role;
import org.dodds.nfrapi.users.User;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;
    private OrgMembership membership;
    private JwtConfig config;

    @BeforeEach
    void setUp() {
        config = new JwtConfig();
        config.setSecret("a-test-secret-that-is-at-least-32-bytes-long!!");
        config.setAccessTokenExpiration(300);     // seconds
        config.setRefreshTokenExpiration(3600);
        jwtService = new JwtService(config);

        user = User.builder()
                .id(UUID.randomUUID())
                .name("Mike")
                .email("mike@example.com")
                .role(Role.USER)               // use one of your real Role values
                .build();

        membership = OrgMembership.builder()
                .organization(Organization.builder().id(UUID.randomUUID()).build())
                .orgRole(null)
                .build();
    }

    @Test
    void shouldGenerateAndParseAccessToken() {
        var token = jwtService.generateAccessToken(user, membership).toString();
        var parsed = jwtService.parseToken(token);

        assertThat(parsed).isNotNull();
        assertThat(parsed.getUserId()).isEqualTo(user.getId());
        assertThat(parsed.getRole()).isEqualTo(user.getRole());
        assertThat(parsed.isExpired()).isFalse();
    }

    @Test
    void shouldGenerateAndParseRefreshTokenToken() {
        var token = jwtService.generateRefreshToken(user, membership).toString();
        var parsed = jwtService.parseToken(token);

        assertThat(parsed).isNotNull();
        assertThat(parsed.getUserId()).isEqualTo(user.getId());
        assertThat(parsed.getRole()).isEqualTo(user.getRole());
        assertThat(parsed.isExpired()).isFalse();
    }

    @Test
    void shouldReturnNullForGarbageToken() {
        assertThat(jwtService.parseToken("not.a.jwt")).isNull();
    }

    @Test
    void shouldIncludeOrgClaimsInAccessToken() {
        var orgId = UUID.randomUUID();
        var membership = OrgMembership.builder()
                .organization(Organization.builder().id(orgId).build())
                .orgRole(OrgRole.ORG_ADMIN)
                .build();

        var token = jwtService.generateAccessToken(user, membership).toString();

        var claims = Jwts.parser()
                .verifyWith(config.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.get("orgId", String.class)).isEqualTo(orgId.toString());
        assertThat(claims.get("orgRole", String.class)).isEqualTo("ORG_ADMIN");
    }

}