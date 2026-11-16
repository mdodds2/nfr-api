package org.dodds.nfrapi.org;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrgMembershipRepository extends JpaRepository<OrgMembership, UUID> {
    List<OrgMembership> findByUserId(UUID userId);
    boolean existsByOrganizationIdAndUserId(UUID orgId, UUID userId);
}