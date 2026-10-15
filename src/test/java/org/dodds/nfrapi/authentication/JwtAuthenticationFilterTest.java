package org.dodds.nfrapi.authentication;

import jakarta.servlet.FilterChain;
import org.dodds.nfrapi.users.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock
    JwtService jwtService;

    @Mock
    FilterChain chain;

    @InjectMocks
    JwtAuthenticationFilter filter;

    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();

    @AfterEach
    void clear() { SecurityContextHolder.clearContext(); }

    @Test
    void shouldAuthenticateValidToken() throws Exception {
        var userId = UUID.randomUUID();
        var jwt = mock(Jwt.class);

        when(jwt.isExpired()).thenReturn(false);
        when(jwt.getUserId()).thenReturn(userId);
        when(jwt.getRole()).thenReturn(Role.ADMIN);
        when(jwtService.parseToken("abc")).thenReturn(jwt);

        request.addHeader("Authorization", "Bearer abc");
        filter.doFilter(request, response, chain);
        var auth = SecurityContextHolder.getContext().getAuthentication();

        assertThat(auth.getPrincipal()).isEqualTo(userId);
        assertThat(auth.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
        verify(chain).doFilter(request, response);
    }

    @Test
    void noHeader() throws Exception {
        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
        verify(jwtService, never()).parseToken(any());
    }

    @Test
    void nonBearerTokenHeader() throws Exception {
        request.addHeader("Authorization", "Basic xyz");
        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
        verify(jwtService, never()).parseToken(any());
    }

    @Test
    void nullToken() throws Exception {
        request.addHeader("Authorization", "Bearer bad");
        when(jwtService.parseToken("bad")).thenReturn(null);

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

    @Test
    void tokenExpired() throws Exception {
        var jwt = mock(Jwt.class);

        when(jwtService.parseToken("abc")).thenReturn(jwt);
        when(jwt.isExpired()).thenReturn(true);

        request.addHeader("Authorization", "Bearer abc");
        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain).doFilter(request, response);
    }

}

