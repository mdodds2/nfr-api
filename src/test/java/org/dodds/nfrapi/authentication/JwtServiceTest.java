package org.dodds.nfrapi.authentication;

import org.dodds.nfrapi.authentication.JwtConfig;
import org.dodds.nfrapi.authentication.JwtService;
import org.dodds.nfrapi.users.Role;
import org.dodds.nfrapi.users.User;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        var config = new JwtConfig();
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
    }

    @Test
    void shouldGenerateAndParseAccessToken() {
        var token = jwtService.generateAccessToken(user).toString();
        var parsed = jwtService.parseToken(token);

        assertThat(parsed).isNotNull();
        assertThat(parsed.getUserId()).isEqualTo(user.getId());
        assertThat(parsed.getRole()).isEqualTo(user.getRole());
        assertThat(parsed.isExpired()).isFalse();
    }

    @Test
    void shouldGenerateAndParseRefreshTokenToken() {
        var token = jwtService.generateRefreshToken(user).toString();
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
}