package org.dodds.nfrapi.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.dodds.nfrapi.org.OrgMembership;
import org.dodds.nfrapi.users.User;
import org.springframework.stereotype.Service;

import java.util.Date;

@AllArgsConstructor
@Service
public class JwtService {

    private final JwtConfig jwtConfig;

    public Jwt generateAccessToken(User user, OrgMembership membership) {
        return generateToken(user, membership, jwtConfig.getAccessTokenExpiration());
    }

    public Jwt generateRefreshToken(User user, OrgMembership membership) {
        return generateToken(user, membership, jwtConfig.getRefreshTokenExpiration());
    }

    private Jwt generateToken(User user, OrgMembership membership, long tokenExpiration) {
        var builder = Jwts.claims()
                .subject(user.getId().toString())
                .add("name", user.getName())
                .add("email", user.getEmail())
                .add("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration));

        if (membership != null) {
            builder.add("orgId", membership.getOrganization().getId().toString())
                    .add("orgRole", membership.getOrgRole().name());
        }

        return new Jwt(builder.build(), jwtConfig.getSecretKey());
    }

    public Jwt parseToken(String token) {
        try {
            var claims = getClaims(token);
            return new Jwt(claims, jwtConfig.getSecretKey());
        } catch(Exception e) {
            System.err.println(e.getMessage());
            return null;
        }

    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


}
