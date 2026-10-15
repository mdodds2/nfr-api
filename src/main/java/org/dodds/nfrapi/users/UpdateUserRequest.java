package org.dodds.nfrapi.users;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    String name;
    String email;
    String password;
}
