package org.dodds.nfrapi.requirement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dodds.nfrapi.group.Group;
import org.hibernate.annotations.GeneratedColumn;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "nfr_requirement")
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rqmt_id")
    private UUID id;

    @Column(name = "rqmt_name")
    private String name;

    @Column(name = "rqmt_description")
    private String description;

    @Column(name = "rqmt_background")
    private String background;

    @Column(name = "is_active", insertable = false, nullable = false)
    private Boolean active;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdMoment;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "updated_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime updatedMoment;

    // we don't want to delete a group if we delete a requirement (ie cascading delete)
    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                                                CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "group_id")
    private Group group;

    public Requirement(UUID id, String name, String description, String background, Boolean active, LocalDateTime createdMoment, LocalDateTime updatedMoment) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.background = background;
        this.active = active;
        this.createdMoment = createdMoment;
        this.updatedMoment = updatedMoment;
    }

    @Override
    public String toString() {
        return "Requirement{" +
                "id=" + id +
                ", name=" + name +
                ", description='" + description + '\'' +
                ", background='" + background + '\'' +
                ", active=" + active +
                ", createdMoment=" + createdMoment +
                ", updatedMoment=" + updatedMoment +
                '}';
    }
}
