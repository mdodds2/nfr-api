package org.dodds.nfrapi.authentication;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.dodds.nfrapi.org.OrgRole;
import org.dodds.nfrapi.users.Role;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;


public class Jwt {
    private final Claims claims;
    private final SecretKey secretKey;

    public Jwt(Claims claims, SecretKey key) {
        this.claims = claims;
        this.secretKey = key;
    }

    public boolean isExpired() {
        return claims.getExpiration().before(new Date());
    }

    public UUID getUserId() {
        return UUID.fromString(claims.getSubject());
    }

    public Role getRole() {
        return Role.valueOf(claims.get("role").toString());
    }

    public UUID getOrgId() {
        String orgId = claims.get("orgId", String.class);
        return orgId == null ? null : UUID.fromString(orgId);
    }

    public OrgRole getOrgRole() {
        String orgRole = claims.get("orgRole", String.class);
        return orgRole == null ? null : OrgRole.valueOf(orgRole);
    }

    @Override
    public String toString() {
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }

}
