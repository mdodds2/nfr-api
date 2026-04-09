package org.dodds.nfrapi.users;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private Role role;
}
