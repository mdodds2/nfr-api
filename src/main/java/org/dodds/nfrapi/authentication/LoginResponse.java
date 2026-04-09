package org.dodds.nfrapi.authentication;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;


@AllArgsConstructor
@Getter
public class LoginResponse {
    private Jwt accessToken;
    private Jwt refreshToken;
}
