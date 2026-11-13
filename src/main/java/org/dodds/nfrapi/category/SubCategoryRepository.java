package org.dodds.nfrapi.category;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, UUID> {
    boolean existsByNameAndCategoryId(String name, UUID categoryId);

    List<SubCategory> findAllByCategoryId(UUID id, Sort by);

    SubCategory findByIdAndCategoryId(UUID id, UUID categoryId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SubCategory s where s.id = :id")
    Optional<SubCategory> findByIdForUpdate(@Param("id") UUID id);
}
