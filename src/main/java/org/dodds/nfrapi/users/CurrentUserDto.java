package org.dodds.nfrapi.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dodds.nfrapi.org.OrgMembershipDto;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserDto {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private List<OrgMembershipDto> orgs;
}
