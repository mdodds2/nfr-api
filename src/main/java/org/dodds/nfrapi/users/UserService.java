package org.dodds.nfrapi.users;

import lombok.AllArgsConstructor;
import org.dodds.nfrapi.org.OrgMembershipMapper;
import org.dodds.nfrapi.org.OrgMembershipRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final OrgMembershipRepository membershipRepository;
    private final UserMapper userMapper;
    private final OrgMembershipMapper membershipMapper;
    private final PasswordEncoder passwordEncoder;

    public List<UserDto> getAllUsers(String sortBy) {

        var sort = "email".equals(sortBy) ? "email" : "name";

        return userRepository.findAll(Sort.by(sort))
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUser(UUID id) {
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userMapper.toDto(user);
    }

    public CurrentUserDto getCurrentUser(UUID id) {
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        var orgs = membershipRepository.findByUserId(id, Sort.by("organization.name"));

        var orgDtos = orgs.stream()
                .map(membershipMapper::toDto)
                .toList();

        return CurrentUserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .orgs(orgDtos)
                .build();
    }

    public UserDto getUserByEmail(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto registerUser(RegisterUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException();
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(UUID id, UpdateUserRequest request) {
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if (!user.getEmail().equalsIgnoreCase(request.getEmail()) && userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        userMapper.update(request, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Transactional
    public void deleteUser(UUID id) {
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
    }

    @Transactional
    public void changePassword(UUID id, ChangePasswordRequest request) {
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AccessDeniedException("Password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

}
