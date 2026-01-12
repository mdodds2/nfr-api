package org.dodds.nfrapi.group;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    GroupRepository groupRepository;

    @Mock
    GroupMapper groupMapper;

    @InjectMocks
    GroupService groupService;

    @Test
    void shouldAddAGroupSuccessfully() {

        // Arrange
        var uuid = UUID.randomUUID();
        var inputDto = new CreateGroupRequest("test group name", "test group description");
        var groupDto = new GroupDto(uuid, "test group name", "test group description", true, LocalDateTime.now(), LocalDateTime.now());
        var group = new Group(uuid, "test group name", "test group description", true, LocalDateTime.now(), LocalDateTime.now());

        //Act
        when(groupRepository.existsByName(anyString())).thenReturn(false);
        when(groupMapper.toEntityFromCreateGroupRequest(any())).thenReturn(group);
        when(groupMapper.toDto(any())).thenReturn(groupDto);
        var resultDto = groupService.createGroup(inputDto);

        //Assert
        assertEquals(group.getId(), resultDto.getId());
        assertEquals(group.getName(), resultDto.getName());
        assertEquals(group.getDescription(), resultDto.getDescription());
        assertEquals(group.getActive(), resultDto.isActive());
        assertEquals(group.getCreatedMoment(), resultDto.getCreatedMoment());
        assertEquals(group.getUpdatedMoment(), resultDto.getUpdatedMoment());
    }

    @Test
    void shouldNotAddAGroupAndThrowAnException() {
        var inputDto = new CreateGroupRequest("test group name", "test group description");
        when(groupRepository.existsByName(anyString())).thenReturn(true);
        assertThrows(DuplicateGroupException.class, () -> groupService.createGroup(inputDto));
    }

    @Test
    void shouldReturnAllGroups() {
        // Arrange

        // Act
        when(groupRepository.findByActive(true)).thenReturn(new ArrayList<>());
        var groupList = groupService.getAllGroups();

        // Assert
        assertNotNull(groupList);
    }

    @Test
    void shouldReturnAGroup () {
        //Arrange
        var uuid = UUID.randomUUID();
        var now = LocalDateTime.now();
        var mockGroup = new Group(uuid,
                "test group name",
                "test group description",
                true,
                now,
                now);

        var mockDto = new GroupDto(uuid,
                "test group name",
                "test group description",
                true,
                now,
                now);

        // Act
        when(groupRepository.findById(uuid)).thenReturn(Optional.of(mockGroup));
        when(groupMapper.toDto(any())).thenReturn(mockDto);
        var groupDto = groupService.getGroup(uuid);

        // Assert
        assertNotNull(groupDto);
        assertEquals(mockGroup.getId(), groupDto.getId());
        assertEquals(mockGroup.getName(), groupDto.getName());
        assertEquals(mockGroup.getDescription(), groupDto.getDescription());
        assertEquals(mockGroup.getActive(), groupDto.isActive());
        assertEquals(mockGroup.getCreatedMoment(), groupDto.getCreatedMoment());
        assertEquals(mockGroup.getUpdatedMoment(), groupDto.getUpdatedMoment());
    }

    @Test
    void shouldNotReturnAGroupAndThrowAnException () {
        var uuid = UUID.randomUUID();
        when(groupRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(GroupNotFoundException.class, () -> groupService.getGroup(uuid));
    }

    @Test
    void shouldUpdateGroup() {
        // Arrange
        var uuid = UUID.randomUUID();
        var group = new Group(uuid,
                "test group name",
                "test group description",
                false,
                LocalDateTime.now(),
                LocalDateTime.now());

        var groupDto = new GroupDto(uuid,
                "test group name",
                "test group description",
                false,
                LocalDateTime.now(),
                LocalDateTime.now());

        UpdateGroupRequest request = new UpdateGroupRequest("test group name", "test group description", false);

        // Act
        when(groupRepository.findById(uuid)).thenReturn(Optional.of(group));
        when(groupRepository.save(group)).thenReturn(group);
        when(groupMapper.toDto(any())).thenReturn(groupDto);
        var result = groupService.updateGroup(uuid, request);

        // Assert
        assertNotNull(request);
        assertEquals(result.getId(), group.getId());
        assertEquals(result.getName(), group.getName());
        assertEquals(result.getDescription(), group.getDescription());
        assertEquals(result.isActive(), group.getActive());
        assertEquals(result.getCreatedMoment(), group.getCreatedMoment());
        assertEquals(result.getUpdatedMoment(), group.getUpdatedMoment());

    }

    @Test
    void shouldNotUpdateGroupAndThrowAnException() {
        var inputDto = new UpdateGroupRequest("test group name", "test group description", false);
        when(groupRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(GroupNotFoundException.class, () ->
            groupService.updateGroup(UUID.randomUUID(), inputDto)
        );
    }

    @Test
    void shouldDeleteGroup() {
        // Arrange
        var uuid = UUID.randomUUID();
        var group = new Group(uuid,
                "test group name",
                "test group description",
                false,
                LocalDateTime.now(),
                LocalDateTime.now());

        // Act
        when(groupRepository.findById(uuid)).thenReturn(Optional.of(group));
        groupService.deleteGroup(uuid);

        // Assert

    }

    @Test
    void shouldNotDeleteGroupAndThrowAnException() {
        var uuid = UUID.randomUUID();
        when(groupRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(GroupNotFoundException.class, () -> groupService.deleteGroup(uuid));
    }

}