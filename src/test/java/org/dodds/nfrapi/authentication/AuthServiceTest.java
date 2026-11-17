package org.dodds.nfrapi.authentication;

import org.dodds.nfrapi.org.OrgMembership;
import org.dodds.nfrapi.org.OrgMembershipRepository;
import org.dodds.nfrapi.org.OrgRole;
import org.dodds.nfrapi.users.User;
import org.dodds.nfrapi.users.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    OrgMembershipRepository membershipRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtService jwtService;

    @InjectMocks
    AuthService authService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldLogin() {
        var request = LoginRequest.builder().email("user@test.com").password("default").build();

        var user = User.builder().id(UUID.randomUUID()).email(request.getEmail()).build();
        var membership = OrgMembership.builder().id(UUID.randomUUID()).orgRole(OrgRole.ORG_ADMIN).build();
        var authentication = mock(Authentication.class);
        var accessToken = mock(Jwt.class);
        var refreshToken = mock(Jwt.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);

        var response = authService.login(request);

        assertThat(response.getAccessToken()).isSameAs(accessToken);
        assertThat(response.getRefreshToken()).isSameAs(refreshToken);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);
    }

    @Test
    void shouldNotLoginBadPassword() {
        var request = LoginRequest.builder().email("user@test.com").password("default").build();
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void shouldNotLoginUserNotFound() {
        var request = LoginRequest.builder().email("user@test.com").password("default").build();
        var authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void shouldRefreshAccessToken() {
        var userId = UUID.randomUUID();
        var user = User.builder().id(userId).build();
        var refreshJwt = mock(Jwt.class);
        var newAccessToken = mock(Jwt.class);

        when(jwtService.parseToken("refresh-token")).thenReturn(refreshJwt);
        when(refreshJwt.isExpired()).thenReturn(false);
        when(refreshJwt.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn(newAccessToken);

        var result = authService.refreshAccessToken("refresh-token");

        assertThat(result).isSameAs(newAccessToken);
    }

    @Test
    void shouldNotRefreshAccessTokenParsedTokenIsNull() {
        when(jwtService.parseToken("refresh-token")).thenReturn(null);
        assertThrows(BadCredentialsException.class, () -> authService.refreshAccessToken("refresh-token"));
        verify(userRepository, never()).findById(any());
    }

    @Test
    void shouldNotRefreshAccessUserNotFound() {
        var userId = UUID.randomUUID();
        var refreshJwt = mock(Jwt.class);

        when(jwtService.parseToken("refresh-token")).thenReturn(refreshJwt);
        when(refreshJwt.isExpired()).thenReturn(false);
        when(refreshJwt.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.refreshAccessToken("refresh-token"));
    }

    @Test
    void shouldGetCurrentUser() {
        var userId = UUID.randomUUID();
        var principal = new UserPrincipal(userId);
        var user = User.builder().id(userId).build();
        var auth = new UsernamePasswordAuthenticationToken(principal, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertThat(authService.getCurrentUser()).isSameAs(user);
    }

    @Test
    void shouldNotLoginUserNotAuthenticated() {
        var request = LoginRequest.builder().email("user@test.com").password("default").build();
        var authentication = mock(Authentication.class);
        var user = User.builder().id(UUID.randomUUID()).build();
        var accessToken = mock(Jwt.class);
        var refreshToken = mock(Jwt.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn(accessToken);
        when(jwtService.generateRefreshToken(user)).thenReturn(refreshToken);

        authService.login(request);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldNotRefreshAccessTokenExpiredToken() {
        var jwt = mock(Jwt.class);
        when(jwtService.parseToken("refresh-token")).thenReturn(jwt);
        when(jwt.isExpired()).thenReturn(true);

        assertThrows(BadCredentialsException.class, () -> authService.refreshAccessToken("refresh-token"));

        verify(userRepository, never()).findById(any());
    }

}
