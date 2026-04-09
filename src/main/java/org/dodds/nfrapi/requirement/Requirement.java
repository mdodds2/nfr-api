package org.dodds.nfrapi.requirement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dodds.nfrapi.category.SubCategory;
import org.dodds.nfrapi.report.Report;
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

    @Column(name = "priority")
    private String priority;

    @Column(name = "status")
    private String status;

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

    @ManyToMany(mappedBy = "reportRequirements")
    private Set<Report> reports = new HashSet<>();

    public Requirement(UUID id, String identifier, String title, String description, String priority, String status, String rationale, String source, String riskIfViolated, LocalDateTime createdMoment, LocalDateTime updatedMoment) {
        this.id = id;
        this.identifier = identifier;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.rationale = rationale;
        this.source = source;
        this.riskIfViolated = riskIfViolated;
        this.createdMoment = createdMoment;
        this.updatedMoment = updatedMoment;
    }

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
