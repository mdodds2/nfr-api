package org.dodds.nfrapi.category;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GeneratedColumn;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "category_sub")
public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    //@Column(name = "category_id", nullable = false)
    //private UUID categoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_name", nullable = false)
    private String shortName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "is_active", insertable = false, nullable = false)
    private Boolean active;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "modified_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    /*
    public SubCategory(UUID id, String name, String shortName, String description, Integer sortOrder, Boolean active, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.sortOrder = sortOrder;
        this.active = active;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
     */

}
