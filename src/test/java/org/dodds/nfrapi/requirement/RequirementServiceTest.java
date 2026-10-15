package org.dodds.nfrapi.requirement;

import org.dodds.nfrapi.category.*;

import org.dodds.nfrapi.common.Priority;
import org.dodds.nfrapi.common.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequirementServiceTest {

    @Mock
    CategoryRepository categoryRepository;

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
        var requirement = Requirement.builder().id(UUID.randomUUID()).title("Requirement 1").build();
        var requirementDto = RequirementDto.builder().id(UUID.randomUUID()).title("Requirement 1").build();

        when(requirementRepository.findAll()).thenReturn(List.of(requirement));
        when(requirementMapper.toDto(requirement)).thenReturn(requirementDto);

        // Act
        var result = requirementService.getAllRequirements();

        // Assert
        assertThat(result).containsExactly(requirementDto);
    }

    @Test
    void shouldReturnARequirement() {
        // Arrange

        SubCategory subCategory = SubCategory.builder()
                .id(UUID.randomUUID())
                .name("test subCategory name")
                .shortName("shortName")
                .description("test subCategory description")
                .sortOrder(1)
                .active(true)
                .build();

        Requirement mockRequirement = Requirement.builder()
                .id(UUID.randomUUID())
                .identifier("test requirement identifier")
                .title("test requirement title")
                .description("test requirement description")
                .priority(Priority.LOW)
                .status(Status.APPROVED)
                .rationale("rationale")
                .source("source")
                .riskIfViolated("riskIfViolated")
                .build();

        mockRequirement.setSubCategory(subCategory);

        RequirementDto mockDto = RequirementDto.builder().
                id(mockRequirement.getId()).
                subCategoryId(subCategory.getId()).
                identifier("test requirement identifier").
                title("test requirement title").
                description("test requirement description").
                priority(Priority.LOW).
                status(Status.APPROVED).
                rationale("rationale").
                source("source").
                riskIfViolated("riskIfViolated").
                build();

        // Act
        when(requirementRepository.findById(mockRequirement.getId())).thenReturn(Optional.of(mockRequirement));
        when(requirementMapper.toDto(any())).thenReturn(mockDto);
        RequirementDto result = requirementService.getRequirement(mockRequirement.getId());

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
    void shouldNotReturnARequirementAndThrowAnException() {
        // Arrange
        var uuid = UUID.randomUUID();

        // Act / Assert
        when(requirementRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(RequirementNotFoundException.class, () -> requirementService.getRequirement(uuid));
    }

    @Test
    void shouldReturnAListOfRequirementsBySubCategoryId() {
        // Arrange
        UUID subCategoryId = UUID.randomUUID();

        var category = Category.builder().id(UUID.randomUUID()).name("Category 1").build();

        var subCategory = SubCategory.builder()
                .id(subCategoryId)
                .name("test subCategory name")
                .shortName("shortName")
                .description("test subCategory description")
                .sortOrder(1)
                .active(true)
                .category(category)
                .build();

        var requirement = Requirement.builder().id(UUID.randomUUID()).title("Requirement 1 ").build();
        var requirementDto = RequirementDto.builder().id(UUID.randomUUID()).title("Requirement 1 ").build();

        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategory));
        when(requirementRepository.findBySubCategoryId(subCategoryId, Sort.by("identifier"))).thenReturn(List.of(requirement));
        when(requirementMapper.toDto(any())).thenReturn(requirementDto);

        // Act
        List<RequirementDto> requirements = requirementService.getRequirementsBySubCategory(subCategoryId, "identifier");

        // Assert
        assertNotNull(requirements);
    }

    @Test
    void shouldReturnAListOfRequirementsBySubCategoryIdSortByOther() {
        // Arrange
        UUID subCategoryId = UUID.randomUUID();

        var category = Category.builder().id(UUID.randomUUID()).name("Category 1").build();

        var subCategory = SubCategory.builder()
                .id(subCategoryId)
                .name("test subCategory name")
                .shortName("shortName")
                .description("test subCategory description")
                .sortOrder(1)
                .active(true)
                .category(category)
                .build();

        var requirement = Requirement.builder().id(UUID.randomUUID()).title("Requirement 1 ").build();
        var requirementDto = RequirementDto.builder().id(UUID.randomUUID()).title("Requirement 1 ").build();

        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategory));
        when(requirementRepository.findBySubCategoryId(subCategoryId, Sort.by("identifier"))).thenReturn(List.of(requirement));
        when(requirementMapper.toDto(any())).thenReturn(requirementDto);

        // Act
        List<RequirementDto> requirements = requirementService.getRequirementsBySubCategory(subCategoryId, "other");

        // Assert
        assertNotNull(requirements);
    }

    @Test
    void shouldNotReturnAListOfRequirementsBySubCategoryIdAndThrowAnException() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act / Assert
        //when(subCategoryRepository.findById(uuid)).thenReturn(Optional.empty());
        when(subCategoryRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(SubCategoryNotFoundException.class, () -> requirementService.getRequirementsBySubCategory(uuid, "title"));
    }

    @Test
    void shouldCreateARequirement() {

        // Arrange
        SubCategory subCategory = SubCategory.builder()
                .id(UUID.randomUUID())
                .name("test subCategory name")
                .shortName("shortName")
                .description("test subCategory description")
                .sortOrder(1)
                .active(true)
                .build();

        CreateRequirementRequest request = CreateRequirementRequest.builder()
                .subCategoryId(subCategory.getId())
                .identifier("test requirement identifier")
                .title("test requirement title")
                .description("test requirement description")
                .priority(Priority.LOW)
                .status(Status.APPROVED)
                .targetValue("value")
                .thresholdValue("threshold")
                .unit("unit")
                .measurementMethod("measurementMethod")
                .rationale("rationale")
                .source("source")
                .riskIfViolated("riskIfViolated")
                .build();

        Requirement mockRequirement = Requirement.builder()
                .id(UUID.randomUUID())
                .identifier("test requirement identifier")
                .title("test requirement title")
                .description("test requirement description")
                .priority(Priority.LOW)
                .status(Status.APPROVED)
                .rationale("rationale")
                .source("source")
                .riskIfViolated("riskIfViolated")
                .build();

        mockRequirement.setSubCategory(subCategory);

        RequirementDto mockDto = RequirementDto.builder()
                .id(mockRequirement.getId())
                .subCategoryId(subCategory.getId())
                .identifier("test requirement identifier")
                .title("test requirement title")
                .description("test requirement description")
                .priority(Priority.LOW)
                .status(Status.APPROVED)
                .rationale("rationale")
                .source("source")
                .riskIfViolated("riskIfViolated")
                .build();

        // Act
        when(requirementRepository.existsByIdentifier(request.getIdentifier())).thenReturn(false);
        when(subCategoryRepository.findById(request.getSubCategoryId())).thenReturn(Optional.of(subCategory));
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
        verify(requirementRepository).saveAndFlush(any());
    }

    @Test
    void shouldNotCreateARequirementAndThrowAnException() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        CreateRequirementRequest request = CreateRequirementRequest.builder()
                .subCategoryId(UUID.randomUUID())
                .identifier("test requirement identifier")
                .title("test requirement title")
                .description("test requirement description")
                .priority(Priority.LOW)
                .status(Status.APPROVED)
                .targetValue("value")
                .thresholdValue("threshold")
                .unit("unit")
                .measurementMethod("measurementMethod")
                .rationale("rationale")
                .source("source")
                .riskIfViolated("riskIfViolated")
                .build();

        // Act / Assert
        when(requirementRepository.existsByIdentifier(request.getIdentifier())).thenReturn(true);
        assertThrows(DuplicateRequirementException.class, () -> requirementService.createRequirement(request));
    }

    @Test
    void shouldNotCreateARequirementAndThrowAnSubCategoryNotFoundException() {
        // Arrange
        SubCategory subCategory = SubCategory.builder()
                .id(UUID.randomUUID())
                .name("test subCategory name")
                .shortName("shortName")
                .description("test subCategory description")
                .sortOrder(1)
                .active(true)
                .build();

        CreateRequirementRequest request = CreateRequirementRequest.builder()
                .subCategoryId(subCategory.getId())
                .identifier("test requirement identifier")
                .title("test requirement title")
                .description("test requirement description")
                .priority(Priority.LOW)
                .status(Status.APPROVED)
                .targetValue("value")
                .thresholdValue("threshold")
                .unit("unit")
                .measurementMethod("measurementMethod")
                .rationale("rationale")
                .source("source")
                .riskIfViolated("riskIfViolated")
                .build();

        Requirement mockRequirement = Requirement.builder()
                .id(UUID.randomUUID())
                .identifier("test requirement identifier")
                .title("test requirement title")
                .description("test requirement description")
                .priority(Priority.LOW)
                .status(Status.APPROVED)
                .rationale("rationale")
                .source("source")
                .riskIfViolated("riskIfViolated")
                .build();

        mockRequirement.setSubCategory(subCategory);

        // Act / Assert
        when(requirementRepository.existsByIdentifier(request.getIdentifier())).thenReturn(false);
        when(subCategoryRepository.findById(mockRequirement.getSubCategory().getId())).thenReturn(Optional.empty());
        assertThrows(SubCategoryNotFoundException.class, () -> requirementService.createRequirement(request));
    }

    @Test
    void shouldUpdateARequirement() {
        // Arrange
        UpdateRequirementRequest request = UpdateRequirementRequest.builder()
                .identifier("test requirement identifier")
                .title("test requirement title")
                .description("test requirement description")
                .priority(Priority.LOW)
                .status(Status.APPROVED)
                .targetValue("value")
                .thresholdValue("threshold")
                .unit("unit")
                .measurementMethod("measurementMethod")
                .rationale("rationale")
                .source("source")
                .riskIfViolated("riskIfViolated")
                .build();

        SubCategory subCategory = SubCategory.builder()
                .id(UUID.randomUUID())
                .name("test subCategory name")
                .shortName("shortName")
                .description("test subCategory description")
                .sortOrder(1)
                .active(true)
                .build();

        Requirement mockRequirement = Requirement.builder()
                .id(UUID.randomUUID())
                .identifier("test requirement identifier")
                .title("test requirement title")
                .description("test requirement description")
                .priority(Priority.LOW)
                .status(Status.APPROVED)
                .rationale("rationale")
                .source("source")
                .riskIfViolated("riskIfViolated")
                .subCategory(subCategory)
                .build();

        RequirementDto mockDto = RequirementDto.builder()
                .id(mockRequirement.getId())
                .subCategoryId(UUID.randomUUID())
                .identifier("test requirement identifier")
                .title("test requirement title")
                .description("test requirement description")
                .priority(Priority.LOW)
                .status(Status.APPROVED)
                .rationale("rationale")
                .source("source")
                .riskIfViolated("riskIfViolated")
                .build();

        // Act
        when(requirementRepository.findById(mockRequirement.getId())).thenReturn(Optional.of(mockRequirement));
        when(requirementMapper.toDto(any())).thenReturn(mockDto);

        // Assert
        assertNotNull(requirementService.updateRequirement(mockRequirement.getId(), request));
        verify(requirementRepository).save(any());
    }

    @Test
    void shouldNotUpdateARequirementAndThrowAnException() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID subCategoryId = UUID.randomUUID();
        UpdateRequirementRequest request = UpdateRequirementRequest.builder()
                .identifier("test requirement identifier")
                .title("test requirement title")
                .description("test requirement description")
                .priority(Priority.LOW)
                .status(Status.APPROVED)
                .targetValue("value")
                .thresholdValue("threshold")
                .unit("unit")
                .measurementMethod("measurementMethod")
                .rationale("rationale")
                .source("source")
                .riskIfViolated("riskIfViolated")
                .build();

        // Act / Assert
        when(requirementRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(RequirementNotFoundException.class, () -> requirementService.updateRequirement(uuid, request));
    }

    @Test
    void shouldDeleteRequirement() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        Requirement mockRequirement = Requirement.builder()
                .id(uuid)
                .identifier("test requirement identifier")
                .title("test requirement title")
                .description("test requirement description")
                .priority(Priority.LOW)
                .status(Status.APPROVED)
                .rationale("rationale")
                .source("source")
                .riskIfViolated("riskIfViolated")
                .build();

        when(requirementRepository.findById(uuid)).thenReturn(Optional.of(mockRequirement));

        // Act
        requirementService.deleteRequirement(uuid);

        // Assert
        verify(requirementRepository).deleteById(any());
    }

    @Test
    void shouldNotDeleteRequirement() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        // Act / Assert
        when(requirementRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(RequirementNotFoundException.class, () -> requirementService.deleteRequirement(uuid));
    }

    @Test
    void shouldGetNextIdentifier() {
        var categoryId = UUID.randomUUID();
        Category category = Category.builder().id(categoryId).shortName("catShortName").build();

        var subCategoryId = UUID.randomUUID();
        var subCategory = SubCategory.builder().id(UUID.randomUUID()).shortName("subCatShortName").build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(subCategoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategory));
        when(requirementRepository.countBySubCategoryId(subCategoryId)).thenReturn(0);

        var result = requirementService.getNextIdentifier(categoryId, subCategoryId);

        assertThat(result).isNotNull();
        assertThat(result.getIdentifier()).isEqualTo("catShortName-subCatShortName-1");
    }
}