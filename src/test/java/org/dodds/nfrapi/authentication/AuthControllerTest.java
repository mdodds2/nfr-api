package org.dodds.nfrapi.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)   // skip JWT/security filters for now
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AuthService authService;

    @MockitoBean
    PasswordResetService passwordResetService;

    @MockitoBean
    JwtConfig jwtConfig;   // JwtAuthenticationFilter is a @Component and needs it

    @MockitoBean
    JwtService jwtService;   // JwtAuthenticationFilter is a @Component and needs it

    @Test
    void shouldLogin() throws Exception {
        var access = mock(Jwt.class);
        var refresh = mock(Jwt.class);

        when(access.toString()).thenReturn("access-token");
        when(refresh.toString()).thenReturn("refresh-token");
        when(authService.login(any())).thenReturn(new LoginResponse(access, refresh));
        when(jwtConfig.getRefreshTokenExpiration()).thenReturn(604800);

        var request = new LoginRequest("mike@test.com", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(cookie().value("refreshToken", "refresh-token"))
                .andExpect(cookie().httpOnly("refreshToken", true))
                .andExpect(cookie().secure("refreshToken", true))
                .andExpect(cookie().path("refreshToken", "/auth/refresh"))
                .andExpect(cookie().maxAge("refreshToken", 604800));
    }

    @Test
    void shouldLoginBadPassword() throws Exception {
        when(authService.login(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        var request = new LoginRequest("mike@test.com", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(cookie().doesNotExist("refreshToken"));
        verify(jwtConfig, never()).getRefreshTokenExpiration();
    }

    @Test
    void shouldLoginMissingPassword() throws Exception {
        when(authService.login(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        var request = new LoginRequest("mike@test.com", null);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(cookie().doesNotExist("refreshToken"));
        verify(authService, never()).login(any());
    }

    @Test
    void shouldReturnRefreshToken() throws Exception {
        var token = mock(Jwt.class);
        when(token.toString()).thenReturn("access-token");

        when(authService.refreshAccessToken("refresh-token")).thenReturn(token);

        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("refreshToken", "refresh-token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"));
    }

    @Test
    void shouldNotReturnRefreshTokenBadToken() throws Exception {

        when(authService.refreshAccessToken("bad-token")).thenThrow(new BadCredentialsException("Invalid refresh token"));

        mockMvc.perform(post("/auth/refresh")
                        .cookie(new Cookie("refreshToken", "bad-token")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldNotReturnMissingCookie() throws Exception {
        var refresh = mock(Jwt.class);
        when(refresh.toString()).thenReturn("refresh-token");

        when(authService.refreshAccessToken("refresh-token")).thenReturn(refresh);

        mockMvc.perform(post("/auth/refresh"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAcceptForgotPassword() throws Exception {

        var request = new ForgotPasswordRequest("user@domain.com");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        verify(passwordResetService).requestReset("user@domain.com");
    }

    @Test
    void shouldResetPassword() throws Exception {
        var request = new ResetPasswordRequest("RAW_TOKEN", "NEW_PASSWORD");
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(passwordResetService).resetPassword("RAW_TOKEN", "NEW_PASSWORD");
    }

}
