package org.dodds.nfrapi.requirement;

import jakarta.persistence.Column;
import org.dodds.nfrapi.category.SubCategory;
import org.dodds.nfrapi.category.SubCategoryNotFoundException;
import org.dodds.nfrapi.category.SubCategoryRepository;

import org.hibernate.annotations.GeneratedColumn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequirementServiceTest {

    @Mock
    SubCategoryRepository subCategoryRepository;

    @Mock
    RequirementRepository requirementRepository;

    @Mock
    RequirementMapper requirementMapper;

    @InjectMocks
    RequirementService requirementService;

    @Test
    void shouldReturnAllRequirements() {
        // Arrange
        when(requirementRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        var requirementsList = requirementService.getAllRequirements();

        // Assert
        assertNotNull(requirementsList);
    }

    @Test
    void shouldReturnARequirement() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID subCategoryId = UUID.randomUUID();

        SubCategory subCategory = new SubCategory(
                uuid,
                "test subCategory name",
                "shortName",
                "test subCategory description",
                1,
                true,
                null,
                null);

        Requirement mockRequirement = new Requirement(
            uuid,
            "test requirement identifier",
            "test requirement title",
            "test requirement description",
            "Low",
            "Approved",
            "rationale",
            "source",
            "riskIfViolated",
            null,
            null
        );
        mockRequirement.setSubCategory(subCategory);

        RequirementDto mockDto = new RequirementDto(
                uuid,
                subCategoryId,
                "test requirement identifier",
                "test requirement title",
                "test requirement description",
                "Low",
                "Approved",
                "rationale",
                "source",
                "riskIfViolated",
                null,
                null,
                null
        );

        // Act
        when(requirementRepository.findById(uuid)).thenReturn(Optional.of(mockRequirement));
        when(requirementMapper.toDto(any())).thenReturn(mockDto);
        RequirementDto result = requirementService.getRequirement(uuid);

        // Assert
        assertNotNull(result);
        assertEquals(result.getId(), mockRequirement.getId());
        assertEquals(result.getIdentifier(), mockRequirement.getIdentifier());
        assertEquals(result.getDescription(), mockRequirement.getDescription());
        assertEquals(result.getPriority(), mockRequirement.getPriority());
        assertEquals(result.getStatus(), mockRequirement.getStatus());
        assertEquals(result.getRationale(), mockRequirement.getRationale());
        assertEquals(result.getSource(), mockRequirement.getSource());
        assertEquals(result.getRiskIfViolated(), mockRequirement.getRiskIfViolated());
    }

    @Test
    void shouldNotReturnARequirementAndThrowAndException() {
        // Arrange
        var uuid = UUID.randomUUID();

        // Act / Assert
        when(requirementRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(RequirementNotFoundException.class, () -> requirementService.getRequirement(uuid));
    }

    @Test
    void shouldReturnAListOfRequirementsBySubCategoryId() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        UUID subCategoryId = UUID.randomUUID();

        SubCategory subCategory = new SubCategory(
                categoryId,
                "test subCategory name",
                "shortName",
                "test subCategory description",
                1,
                true,
                null,
                null,
                null
        );


        // Act
        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategory));
        List<RequirementDto> requirements = requirementService.getRequirementsBySubCategory(subCategoryId, "");

        // Assert
        assertNotNull(requirements);
    }

    @Test
    void shouldNotReturnAListOfRequirementsByGroupIdAndThrowAnException() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act / Assert
        when(subCategoryRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(SubCategoryNotFoundException.class, () -> requirementService.getRequirementsBySubCategory(uuid, null));
    }

    @Test
    void shouldCreateARequirement() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID subCategoryId = UUID.randomUUID();

        CreateRequirementRequest request = new CreateRequirementRequest(
            subCategoryId,
            "test requirement identifier",
            "test requirement title",
            "test requirement description",
            "Low",
            "Approved",
            "value",
            "threshold",
            "unit",
            "measurementMethod",
            "rationale",
            "source",
            "riskIfViolated"
        );

        SubCategory subCategory = new SubCategory(
                uuid,
                "test subCategory name",
                "shortName",
                "test subCategory description",
                1,
                true,
                null,
                null
        );

        Requirement mockRequirement = new Requirement(
                uuid,
                "test requirement identifier",
                "test requirement title",
                "test requirement description",
                "Low",
                "Approved",
                "rationale",
                "source",
                "riskIfViolated",
                null,
                null
        );

        mockRequirement.setSubCategory(subCategory);

        RequirementDto mockDto = new RequirementDto(
            uuid,
            subCategoryId,
            "test requirement identifier",
            "test requirement title",
            "test requirement description",
            "Low",
            "Approved",
            "rationale",
            "source",
            "riskIfViolated",
            null,
            null,
            null
        );

        // Act
        when(requirementRepository.existsByTitle(anyString())).thenReturn(false);
        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategory));
        when(requirementMapper.toEntityFromCreateRequirementRequest(request)).thenReturn(mockRequirement);
        when(requirementMapper.toDto(mockRequirement)).thenReturn(mockDto);
        RequirementDto result = requirementService.createRequirement(request);

        // Assert
        assertNotNull(result);
        assertEquals(result.getSubCategoryId(), request.getSubCategoryId());
        assertEquals(result.getIdentifier(), request.getIdentifier());
        assertEquals(result.getTitle(), request.getTitle());
        assertEquals(result.getDescription(), request.getDescription());
        assertEquals(result.getPriority(), request.getPriority());
        assertEquals(result.getStatus(), request.getStatus());
        assertEquals(result.getRationale(), request.getRationale());
        assertEquals(result.getSource(), request.getSource());
        assertEquals(result.getRiskIfViolated(), request.getRiskIfViolated());
    }

    @Test
    void shouldNotCreateARequirementAndThrowAnException() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        CreateRequirementRequest request = new CreateRequirementRequest(
                uuid,
                "test requirement identifier",
                "test requirement title",
                "test requirement description",
                "Low",
                "Approved",
                "value",
                "threshold",
                "unit",
                "measurementMethod",
                "rationale",
                "source",
                "riskIfViolated"
        );

        // Act / Assert
        when(requirementRepository.existsByTitle(any())).thenReturn(true);
        assertThrows(DuplicateRequirementException.class, () -> requirementService.createRequirement(request));
    }

    @Test
    void shouldNotCreateARequirementAndThrowAnException2() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID subCategoryId = UUID.randomUUID();

        CreateRequirementRequest request = new CreateRequirementRequest(
            uuid,
            "test requirement identifier",
            "test requirement title",
            "test requirement description",
            "Low",
            "Approved",
            "value",
            "threshold",
            "unit",
            "measurementMethod",
            "rationale",
            "source",
            "riskIfViolated"
        );

        SubCategory subCategory = new SubCategory(
            uuid,
            "test subCategory name",
            "shortName",
            "test subCategory description",
            1,
            true,
            null,
            null
        );

        Requirement mockRequirement = new Requirement(
            uuid,
            "test requirement identifier",
            "test requirement title",
            "test requirement description",
            "Low",
            "Approved",
            "rationale",
            "source",
            "riskIfViolated",
            null,
            null
        );
        mockRequirement.setSubCategory(subCategory);

        // Act / Assert
        when(requirementRepository.existsByTitle(any())).thenReturn(false);
        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.empty());
        when(requirementMapper.toEntityFromCreateRequirementRequest(request)).thenReturn(mockRequirement);
        assertThrows(SubCategoryNotFoundException.class, () -> requirementService.createRequirement(request));
    }

    @Test
    void shouldUpdateARequirement() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID subCategoryId = UUID.randomUUID();

        UpdateRequirementRequest request = new UpdateRequirementRequest(
                "test requirement identifier",
                "test requirement title",
                "test requirement description",
                "Low",
                "Approved",
                "value",
                "threshold",
                "unit",
                "measurementMethod",
                "rationale",
                "source",
                "riskIfViolated"
        );

        SubCategory subCategory = new SubCategory(
                uuid,
                "test subCategory name",
                "shortName",
                "test subCategory description",
                1,
                true,
                null,
                null
        );

        Requirement mockRequirement = new Requirement(
                uuid,
                "test requirement identifier",
                "test requirement title",
                "test requirement description",
                "Low",
                "Approved",
                "rationale",
                "source",
                "riskIfViolated",
                null,
                null
        );
        mockRequirement.setSubCategory(subCategory);

        RequirementDto mockDto = new RequirementDto(
                uuid,
                subCategoryId,
                "test requirement identifier",
                "test requirement title",
                "test requirement description",
                "Low",
                "Approved",
                "rationale",
                "source",
                "riskIfViolated",
                null,
                null,
                null
        );

        // Act
        when(requirementRepository.findById(uuid)).thenReturn(Optional.of(mockRequirement));
        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategory));
        //when(requirementMapper.toEntityFromUpdateRequirementRequest(any())).thenReturn(mockRequirement);
        when(requirementMapper.toDto(any())).thenReturn(mockDto);
        var result = requirementService.updateRequirement(uuid, request);

        // Assert
        assertNotNull(result);
    }

    @Test
    void shouldNotUpdateARequirementAndThrowAnException() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID subCategoryId = UUID.randomUUID();
        UpdateRequirementRequest request = new UpdateRequirementRequest(
                "test requirement identifier",
                "test requirement title",
                "test requirement description",
                "Low",
                "Approved",
                "value",
                "threshold",
                "unit",
                "measurementMethod",
                "rationale",
                "source",
                "riskIfViolated"
        );

        // Act / Assert
        when(requirementRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(RequirementNotFoundException.class, () -> requirementService.updateRequirement(uuid, request));
    }

    @Test
    void shouldNotUpdateARequirementAndThrowAnException2() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID subCategoryId = UUID.randomUUID();
        UpdateRequirementRequest request = new UpdateRequirementRequest(
                "test requirement identifier",
                "test requirement title",
                "test requirement description",
                "Low",
                "Approved",
                "value",
                "threshold",
                "unit",
                "measurementMethod",
                "rationale",
                "source",
                "riskIfViolated"
        );

        SubCategory subCategory = new SubCategory(
                uuid,
                "test subCategory name",
                "shortName",
                "test subCategory description",
                1,
                true,
                null,
                null
        );

        Requirement mockRequirement = new Requirement(
                uuid,
                "test requirement identifier",
                "test requirement title",
                "test requirement description",
                "Low",
                "Approved",
                "rationale",
                "source",
                "riskIfViolated",
                null,
                null
        );
        mockRequirement.setSubCategory(subCategory);

        // Act / Assert
        when(requirementRepository.findById(uuid)).thenReturn(Optional.of(mockRequirement));
        assertThrows(SubCategoryNotFoundException.class, () -> requirementService.updateRequirement(uuid, request));
    }

    @Test
    void shouldDeleteRequirement() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        Requirement mockRequirement = new Requirement(
                uuid,
                "test requirement identifier",
                "test requirement title",
                "test requirement description",
                "Low",
                "Approved",
                "rationale",
                "source",
                "riskIfViolated",
                null,
                null
        );

        // Act
        when(requirementRepository.findById(uuid)).thenReturn(Optional.of(mockRequirement));
        requirementService.deleteRequirement(uuid);

        // Assert
        }

    @Test
    void shouldNotDeleteRequirement() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act / Assert
        when(requirementRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(RequirementNotFoundException.class, () -> requirementService.deleteRequirement(uuid));
    }

}