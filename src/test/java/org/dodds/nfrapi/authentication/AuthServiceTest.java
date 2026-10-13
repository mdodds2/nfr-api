package org.dodds.nfrapi.authentication;

import org.dodds.nfrapi.authentication.*;
import org.dodds.nfrapi.category.CategoryNotFoundException;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    JwtService jwtService;

    @InjectMocks
    AuthService authService;

    @AfterEach
    void tearDown() { SecurityContextHolder.clearContext(); }

    @Test
    void shouldLogin() throws Exception {
        var request = LoginRequest.builder().email("mike@test.com").password("default").build();

        var user = User.builder().id(UUID.randomUUID()).email(request.getEmail()).build();
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
    void shouldNotLoginBadPassword() throws Exception {
        var request = LoginRequest.builder().email("mike@test.com").password("default").build();
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));
        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        verify(jwtService, never()).generateAccessToken(any());
    }

    @Test
    void shouldNotLoginUserNotFound() throws Exception {
        var request = LoginRequest.builder().email("mike@test.com").password("default").build();
        var authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> authService.login(request));
    }

    @Test
    void shouldRefreshAccessToken() throws Exception {
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
    void shouldNotRefreshAccessTokenGarbageToken() throws Exception {
        when(jwtService.parseToken("refresh-token")).thenReturn(null);
        assertThrows(BadCredentialsException.class, () -> authService.refreshAccessToken("refresh-token"));
    }

    @Test
    void shouldNotRefreshAccessUserNotFound() throws Exception {
        var userId = UUID.randomUUID();
        var refreshJwt = mock(Jwt.class);

        when(jwtService.parseToken("refresh-token")).thenReturn(refreshJwt);
        when(refreshJwt.isExpired()).thenReturn(false);
        when(refreshJwt.getUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> authService.refreshAccessToken("refresh-token"));
    }

    @Test
    void shouldGetCurrentUser() {
        var userId = UUID.randomUUID();
        var user = User.builder().id(userId).build();
        var auth = new UsernamePasswordAuthenticationToken(userId, null, List.of());

        SecurityContextHolder.getContext().setAuthentication(auth);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertThat(authService.getCurrentUser()).isSameAs(user);
    }

}
