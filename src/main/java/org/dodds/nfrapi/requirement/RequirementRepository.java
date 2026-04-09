package org.dodds.nfrapi.requirement;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, UUID> {
    List<Requirement> findBySubCategoryId(UUID subCategoryId, Sort by);
    boolean existsByIdentifier(String title);
    boolean existsByTitle(String title);
    int countBySubCategoryId(UUID subCategoryId);
}
