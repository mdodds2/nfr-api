package org.dodds.nfrapi.report;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GeneratedColumn;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "measurements")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "requirementId")
    private UUID requirementId;

    @Column(name = "targetValue")
    private String targetValue;

    @Column(name = "thresholdValue")
    private String thresholdValue;

    @Column(name = "unit")
    private String unit;

    @Column(name = "measurement_method")
    private String measurementMethod;

    @Column(name = "stimulus")
    private String trigger;

    @Column(name = "context")
    private String context;

    @Column(name = "system_response")
    private String systemResponse;

    @Column(name = "owner")
    private String owner;

    @Column(name = "acceptance_criteria")
    private String acceptanceCriteria;

    @Column(name = "notes")
    private String notes;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdMoment;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "updated_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime updatedMoment;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;
}
