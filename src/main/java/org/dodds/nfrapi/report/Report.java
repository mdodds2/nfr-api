package org.dodds.nfrapi.report;

import jakarta.persistence.*;
import lombok.*;
import org.dodds.nfrapi.requirement.Requirement;
import org.hibernate.annotations.GeneratedColumn;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reports")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @GeneratedColumn(value = "1")
    @Column(name = "is_active", insertable = false, nullable = false)
    private Boolean active;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "modified_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime modifiedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "reports_requirements",
            joinColumns = @JoinColumn(name = "report_id"),
            inverseJoinColumns = @JoinColumn(name = "requirement_id"))
    private List<Requirement> reportRequirements;

    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Measurement> measurements = new HashSet<>();


}
