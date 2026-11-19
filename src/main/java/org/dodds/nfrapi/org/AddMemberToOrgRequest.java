package org.dodds.nfrapi.org;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMemberToOrgRequest {
    @NotBlank
    @Email(regexp = ".+@.+\\..+")
    private String email;

    @NotNull
    private OrgRole orgRole;

}
