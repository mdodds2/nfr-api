package org.dodds.nfrapi.category;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GeneratedColumn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

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

    @Column(name = "image", nullable = false)
    private String image;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "created_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @GeneratedColumn(value = "CURRENT_TIMESTAMP")
    @Column(name = "modified_at", insertable = false, updatable = false, nullable = false)
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy="category")
    private List<SubCategory> subCategories;
}
