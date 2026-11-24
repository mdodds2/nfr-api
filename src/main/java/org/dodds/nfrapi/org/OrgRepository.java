package org.dodds.nfrapi.org;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrgRepository extends JpaRepository<Organization, UUID> {
}
