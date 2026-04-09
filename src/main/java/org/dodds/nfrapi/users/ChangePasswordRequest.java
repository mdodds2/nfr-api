package org.dodds.nfrapi.users;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    String oldPassword;
    String newPassword;
}
