package org.dodds.nfrapi.requirement;

import jakarta.persistence.*;
import lombok.*;
import org.dodds.nfrapi.category.SubCategory;
import org.dodds.nfrapi.common.Priority;
import org.dodds.nfrapi.common.Status;
import org.dodds.nfrapi.report.Report;
import org.hibernate.annotations.GeneratedColumn;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "requirements")
public class Requirement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "nfr_id")
    private UUID id;

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "rationale")
    private String rationale;

    @Column(name = "source")
    private String source;

    @Column(name = "risk_if_violated")
    private String riskIfViolated;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdMoment;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "updated_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime updatedMoment;

    // we don't want to delete a group if we delete a requirement (ie cascading delete)
    @ManyToOne(fetch=FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE,
                                                CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "category_sub_id")
    private SubCategory subCategory;

    @Builder.Default
    @ManyToMany(mappedBy = "reportRequirements")
    private Set<Report> reports = new HashSet<>();

    @Override
    public String toString() {
        return "Requirement{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", rationale='" + rationale + '\'' +
                ", source='" + source + '\'' +
                ", riskIfViolated='" + riskIfViolated + '\'' +
                ", createdMoment=" + createdMoment +
                ", updatedMoment=" + updatedMoment +
                ", subCategory=" + subCategory +
                '}';
    }
}
