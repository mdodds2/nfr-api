package org.dodds.nfrapi.group;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    public List<Group> getAllGroups() {
        return groupRepository.findByActive(true);
    }

    public GroupDto getGroup(UUID groupId) {
        var group = groupRepository.findById(groupId).orElseThrow(GroupNotFoundException::new);
        return groupMapper.toDto(group);
    }

    @Transactional
    public GroupDto createGroup(CreateGroupRequest request) {
        if(groupRepository.existsByName(request.getName()))
            throw new DuplicateGroupException();
        var group = groupMapper.toEntityFromCreateGroupRequest(request);
        groupRepository.saveAndFlush(group);
        return groupMapper.toDto(group);
    }

    @Transactional
    public GroupDto updateGroup(UUID id, UpdateGroupRequest request) {
        var group = groupRepository.findById(id).orElseThrow(GroupNotFoundException::new);
        groupMapper.update(request, group);
        groupRepository.save(group);
        return groupMapper.toDto(group);
    }

    public void deleteGroup(UUID id) {
        var group = groupRepository.findById(id).orElseThrow(GroupNotFoundException::new);
        groupRepository.delete(group);
    }
}
