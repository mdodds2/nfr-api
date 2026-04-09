package org.dodds.nfrapi.users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UpdateUserRequest {
    String name;
    String email;
    String password;
}
