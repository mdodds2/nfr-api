package org.dodds.nfrapi.group;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class UpdateGroupRequest {
    private String name;
    private String description;
    private boolean active;

    @Override
    public String toString() {
        return "UpdateGroupRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", active=" + active +
                '}';
    }
}
