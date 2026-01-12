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

//    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
//    @JoinColumn(name = "group_id", nullable = false)
//    private Group group;

    @Column(name = "group_id")
    private UUID groupId;

    @Column(name = "rqmt_name")
    private String name;

    @Column(name = "rqmt_description")
    private String description;

    @Column(name = "rqmt_background")
    private String background;

    @GeneratedColumn(value = "1")
    @Column(name = "is_active", insertable = false, nullable = false)
    private Boolean active;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdMoment;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "updated_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime updatedMoment;

    @Override
    public String toString() {
        return "Requirement{" +
                "id=" + id +
                ", group=" + groupId +
                ", name=" + name +
                ", description='" + description + '\'' +
                ", background='" + background + '\'' +
                ", active=" + active +
                ", createdMoment=" + createdMoment +
                ", updatedMoment=" + updatedMoment +
                '}';
    }
}
