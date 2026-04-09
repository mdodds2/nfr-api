package org.dodds.nfrapi.category;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, UUID> {
    boolean existsByName(String name);

    List<SubCategory> findAllByCategoryId(UUID id, Sort by);

    SubCategory findByIdAndCategoryId(UUID id, UUID categoryId);
}
