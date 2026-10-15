package org.dodds.nfrapi.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    String oldPassword;
    String newPassword;
}
