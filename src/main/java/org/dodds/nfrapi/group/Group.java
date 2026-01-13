package org.dodds.nfrapi.group;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dodds.nfrapi.requirement.Requirement;
import org.hibernate.annotations.GeneratedColumn;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "nfr_group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "group_id")
    private UUID id;

    @Column(name = "group_name")
    private String name;

    @Column(name = "group_desc")
    private String description;

    @Column(name = "is_active", insertable = false, nullable = false)
    private Boolean active;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdMoment;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "updated_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime updatedMoment;

    // we don't want to delete requirements if we delete a group
    @OneToMany(mappedBy = "group",
               cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    private List<Requirement> requirements;

    public Group(UUID id, String name, String description, Boolean active, LocalDateTime createdMoment, LocalDateTime updatedMoment) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdMoment = createdMoment;
        this.updatedMoment = updatedMoment;
    }

    // set up bidirectional
    public void add(Requirement requirement) {
        if(requirements == null) {
            requirements = new ArrayList<>();
        }
        requirements.add(requirement);
        requirement.setGroup(this);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", active=" + active +
                ", createdMoment=" + createdMoment +
                ", updatedMoment=" + updatedMoment +
                '}';
    }

}
