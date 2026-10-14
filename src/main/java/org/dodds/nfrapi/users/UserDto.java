package org.dodds.nfrapi.users;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private UUID id;
    private String name;
    private String email;
    private Role role;
}
