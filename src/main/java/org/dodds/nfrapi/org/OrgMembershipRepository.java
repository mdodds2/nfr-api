package org.dodds.nfrapi.org;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrgMembershipRepository extends JpaRepository<OrgMembership, UUID> {
    Optional<OrgMembership> findFirstByUserIdOrderByOrganizationNameAsc(UUID userId);

    Optional<OrgMembership> findByOrganizationIdAndUserId(UUID orgId, UUID userId);

    @EntityGraph(attributePaths = "organization")
    List<OrgMembership> findByUserId(UUID userId, Sort sortBy);

    boolean existsByOrganizationIdAndUserId(UUID orgId, UUID userId);

    @EntityGraph(attributePaths = "user")
    List<OrgMembership> findByOrganizationId(UUID orgId, Sort sortBy);

    long countByOrganizationIdAndOrgRole(UUID orgId, OrgRole orgRole);
}