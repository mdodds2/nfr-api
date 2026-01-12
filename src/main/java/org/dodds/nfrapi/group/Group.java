package org.dodds.nfrapi.group;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dodds.nfrapi.requirement.Requirement;
import org.hibernate.annotations.GeneratedColumn;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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

    @GeneratedColumn(value = "1")
    @Column(name = "is_active", insertable = false, nullable = false)
    private Boolean active;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdMoment;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "updated_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime updatedMoment;

//    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
//    private Set<Requirement> requirements = new HashSet<>();

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
