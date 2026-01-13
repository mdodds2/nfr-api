package org.dodds.nfrapi.requirement;

import org.dodds.nfrapi.group.Group;
import org.dodds.nfrapi.group.GroupNotFoundException;
import org.dodds.nfrapi.group.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    GroupRepository groupRepository;

    @Mock
    RequirementRepository requirementRepository;

    @Mock
    RequirementMapper requirementMapper;

    @InjectMocks
    RequirementService requirementService;

    @Test
    void shouldReturnAllRequirements() {
        // Arrange
        when(requirementRepository.findByActive(true)).thenReturn(new ArrayList<>());

        // Act
        var requirementsList = requirementService.getAllRequirements();

        // Assert
        assertNotNull(requirementsList);
    }

    @Test
    void shouldReturnARequirement() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();

        Group group = new Group(groupId,
                "test group name",
                "test group description",
                true,
                null,
                null);

        Requirement mockRequirement = new Requirement(uuid,
                "test requirement name",
                "test requirement description",
                "test requirement background",
                true,
                null,
                null);
        mockRequirement.setGroup(group);

        RequirementDto mockDto = new RequirementDto(uuid,
                uuid,
                "test requirement name",
                "test requirement description",
                "test requirement background",
                true,
                null,
                null);

        // Act
        when(requirementRepository.findById(uuid)).thenReturn(Optional.of(mockRequirement));
        when(requirementMapper.toDto(any())).thenReturn(mockDto);
        RequirementDto result = requirementService.getRequirement(uuid);

        // Assert
        assertNotNull(result);
        assertEquals(result.getId(), mockRequirement.getId());
        assertEquals(result.getName(), mockRequirement.getName());
        assertEquals(result.getDescription(), mockRequirement.getDescription());
        assertEquals(result.getBackground(), mockRequirement.getBackground());
        assertEquals(result.getActive(), mockRequirement.getActive());
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
    void shouldReturnAListOfRequirementsByGroupId() {
        // Arrange
        UUID groupId = UUID.randomUUID();

        Group group = new Group(groupId,
                "test group name",
                "test group description",
                true,
                null,
                null);

        // Act
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(requirementRepository.findByGroupId(groupId)).thenReturn(new ArrayList<>());
        List<RequirementDto> requirements = requirementService.getRequirementsByGroup(groupId);

        // Assert
        assertNotNull(requirements);
    }

    @Test
    void shouldNotReturnAListOfRequirementsByGroupIdAndThrowAnException() {
        // Arrange
        UUID groupId = UUID.randomUUID();

        // Act / Assert
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());
        assertThrows(GroupNotFoundException.class, () -> requirementService.getRequirementsByGroup(groupId));
    }

    @Test
    void shouldCreateARequirement() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();

        CreateRequirementRequest request = new CreateRequirementRequest(
                groupId,
                "test requirement name",
                "test requirement description",
                "test requirement background"
        );

        Group group = new Group(
                groupId,
                "test group name",
                "test group description",
                true,
                null,
                null);

        Requirement mockRequirement = new Requirement(
                uuid,
                "test requirement name",
                "test requirement description",
                "test requirement background",
                true,
                null,
                null);
        mockRequirement.setGroup(group);

        RequirementDto mockDto = new RequirementDto(
                uuid,
                groupId,
                "test requirement name",
                "test requirement description",
                "test requirement background",
                true,
                null,
                null);

        // Act
        when(requirementRepository.existsByName(anyString())).thenReturn(false);
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(requirementMapper.toEntityFromCreateRequirementRequest(request)).thenReturn(mockRequirement);
        when(requirementMapper.toDto(mockRequirement)).thenReturn(mockDto);
        RequirementDto result = requirementService.createRequirement(request);

        // Assert
        assertNotNull(result);
        assertEquals(result.getGroupId(), request.getGroupId());
        assertEquals(result.getName(), request.getName());
        assertEquals(result.getDescription(), request.getDescription());
        assertEquals(result.getBackground(), request.getBackground());
    }

    @Test
    void shouldNotCreateARequirementAndThrowAnException() {
        // Arrange
        UUID groupId = UUID.randomUUID();

        CreateRequirementRequest request = new CreateRequirementRequest(
                groupId,
                "test requirement name",
                "test requirement description",
                "test requirement background"
        );

        // Act / Assert
        when(requirementRepository.existsByName(any())).thenReturn(true);
        assertThrows(DuplicateRequirementException.class, () -> requirementService.createRequirement(request));
    }

    @Test
    void shouldNotCreateARequirementAndThrowAnException2() {
        // Arrange
        UUID groupId = UUID.randomUUID();

        CreateRequirementRequest request = new CreateRequirementRequest(
                groupId,
                "test requirement name",
                "test requirement description",
                "test requirement background"
        );

        Group group = new Group(
                groupId,
                "test group name",
                "test group description",
                true,
                null,
                null);

        Requirement mockRequirement = new Requirement(
                UUID.randomUUID(),
                "test requirement name",
                "test requirement description",
                "test requirement background",
                true,
                null,
                null);
        mockRequirement.setGroup(group);

        // Act / Assert
        when(requirementRepository.existsByName(any())).thenReturn(false);
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());
        when(requirementMapper.toEntityFromCreateRequirementRequest(request)).thenReturn(mockRequirement);
        assertThrows(GroupNotFoundException.class, () -> requirementService.createRequirement(request));
    }

    @Test
    void shouldUpdateARequirement() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UpdateRequirementRequest request = new UpdateRequirementRequest(
                groupId,
                "test request name",
                "test request description",
                "test request background",
                true);

        Group group = new Group(
                groupId,
                "test group name",
                "test group description",
                true,
                null,
                null);

        Requirement mockRequirement = new Requirement(
                UUID.randomUUID(),
                "test requirement name",
                "test requirement description",
                "test requirement background",
                true,
                null,
                null);
        mockRequirement.setGroup(group);

        RequirementDto mockDto = new RequirementDto(
                uuid,
                groupId,
                "test requirement name",
                "test requirement description",
                "test requirement background",
                true,
                null,
                null);

        // Act
        when(requirementRepository.findById(uuid)).thenReturn(Optional.of(mockRequirement));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
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
        UUID groupId = UUID.randomUUID();
        UpdateRequirementRequest request = new UpdateRequirementRequest(
                groupId,
                "test request name",
                "test request description",
                "test request background",
                true);

        // Act / Assert
        when(requirementRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(RequirementNotFoundException.class, () -> requirementService.updateRequirement(uuid, request));
    }

    @Test
    void shouldNotUpdateARequirementAndThrowAnException2() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UpdateRequirementRequest request = new UpdateRequirementRequest(
                groupId,
                "test request name",
                "test request description",
                "test request background",
                true);

        Group group = new Group(
                groupId,
                "test group name",
                "test group description",
                true,
                null,
                null);

        Requirement mockRequirement = new Requirement(
                UUID.randomUUID(),
                "test requirement name",
                "test requirement description",
                "test requirement background",
                true,
                null,
                null);
        mockRequirement.setGroup(group);

        // Act / Assert
        when(requirementRepository.findById(uuid)).thenReturn(Optional.of(mockRequirement));
        assertThrows(GroupNotFoundException.class, () -> requirementService.updateRequirement(uuid, request));
    }

    @Test
    void shouldDeleteRequirement() {
        // Arrange
        UUID uuid = UUID.randomUUID();

        Requirement mockRequirement = new Requirement(
                UUID.randomUUID(),
                "test requirement name",
                "test requirement description",
                "test requirement background",
                true,
                null,
                null);

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