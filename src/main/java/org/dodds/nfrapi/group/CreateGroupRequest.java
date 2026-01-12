package org.dodds.nfrapi.group;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateGroupRequest {
    private String name;
    private String description;

    @Override
    public String toString() {
        return "UpdateGroupRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
