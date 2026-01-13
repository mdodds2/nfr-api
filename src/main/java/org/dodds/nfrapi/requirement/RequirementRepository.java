package org.dodds.nfrapi.requirement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, UUID> {
    List<Requirement> findByActive(boolean isActive);
    List<Requirement> findByGroupId(UUID groupId);
    boolean existsByName(String name);
}
