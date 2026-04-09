package org.dodds.nfrapi.report;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    boolean existsByUserIdAndName(UUID userId, String name);
    List<Report> findAllByUserId(UUID userId, Sort sortBy);
}
