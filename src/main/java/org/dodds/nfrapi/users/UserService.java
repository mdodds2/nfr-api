package org.dodds.nfrapi.users;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public Iterable<UserDto> getAllUsers(String sortBy) {

        if (!Set.of("name", "email").contains(sortBy))
            sortBy = "name";

        return userRepository.findAll(Sort.by(sortBy))
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUser(UUID id) {
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userMapper.toDto(user);
    }

    public UserDto getUserByEmail(String email) {
        var user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return userMapper.toDto(user);
    }

    public UserDto registerUser(RegisterUserRequest request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException();
        }

        var user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto updateUser(UUID id, UpdateUserRequest request) {
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userMapper.update(request, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public void deleteUser(UUID id) {
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
    }

    public void changePassword(UUID id, ChangePasswordRequest request) {
        var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AccessDeniedException("Password does not match");
        }

        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }

}
