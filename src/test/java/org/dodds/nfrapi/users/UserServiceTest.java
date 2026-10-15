package org.dodds.nfrapi.users;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper mapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void shouldGetAllUsersSortedByName() {
        var user1 = User.builder().id(UUID.randomUUID()).name("Test User1").build();
        var user2 = User.builder().id(UUID.randomUUID()).name("Test User2").build();
        var user3 = User.builder().id(UUID.randomUUID()).name("Test User3").build();

        var userDto1 = UserDto.builder().id(UUID.randomUUID()).name("Test User1").build();
        var userDto2 = UserDto.builder().id(UUID.randomUUID()).name("Test User2").build();
        var userDto3 = UserDto.builder().id(UUID.randomUUID()).name("Test User3").build();

        when(userRepository.findAll(Sort.by("name"))).thenReturn(List.of(user1, user2, user3));
        when(mapper.toDto(user1)).thenReturn(userDto1);
        when(mapper.toDto(user2)).thenReturn(userDto2);
        when(mapper.toDto(user3)).thenReturn(userDto3);

        var results = userService.getAllUsers("name");

        assertThat(results).containsExactly(userDto1, userDto2, userDto3);
    }

    @Test
    void shouldGetAllUsersSortedByEmail() {
        var user = User.builder().id(UUID.randomUUID()).name("Test User").build();
        var userDto = UserDto.builder().id(UUID.randomUUID()).name("Test User").build();

        when(userRepository.findAll(Sort.by("email"))).thenReturn(List.of(user));
        when(mapper.toDto(user)).thenReturn(userDto);

        var results = userService.getAllUsers("email");

        assertThat(results).containsExactly(userDto);
    }

    @Test
    void shouldGetAllUsersSortedByOther() {
        var user = User.builder().id(UUID.randomUUID()).name("Test User").build();
        var userDto = UserDto.builder().id(UUID.randomUUID()).name("Test User").build();

        when(userRepository.findAll(Sort.by("name"))).thenReturn(List.of(user));
        when(mapper.toDto(user)).thenReturn(userDto);

        var results = userService.getAllUsers("other");

        assertNotNull(results);
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldGetAllUsersSortedByNull() {
        var user = User.builder().id(UUID.randomUUID()).name("Test User").build();
        var userDto = UserDto.builder().id(UUID.randomUUID()).name("Test User").build();

        when(userRepository.findAll(Sort.by("name"))).thenReturn(List.of(user));
        when(mapper.toDto(user)).thenReturn(userDto);

        var results = userService.getAllUsers(null);

        assertThat(results).containsExactly(userDto);
    }

    @Test
    void shouldChangePassword() {
        var id = UUID.randomUUID();
        var user = User.builder().id(id).name("Test User").build();
        var request = ChangePasswordRequest.builder().oldPassword("OLD").newPassword("NEW").build();

        when(userRepository.findById(eq(id))).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("NEW")).thenReturn("ENCODED");
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        userService.changePassword(id, request);

        verify(userRepository).save(user);
        assertThat(user.getPassword()).isEqualTo("ENCODED");
    }

    @Test
    void shouldNotChangePasswordUserNotFound() {
        var id = UUID.randomUUID();
        var request = ChangePasswordRequest.builder().oldPassword("OLD").newPassword("NEW").build();

        when(userRepository.findById(eq(id))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.changePassword(id, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldNotChangePasswordNotMatching() {
        var id = UUID.randomUUID();
        var user = User.builder().id(id).name("Test User").password("OTHER").build();
        var request = ChangePasswordRequest.builder().oldPassword(user.getPassword()).newPassword("NEW").build();

        when(userRepository.findById(eq(id))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> userService.changePassword(id, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldDeleteUser() {
        var id = UUID.randomUUID();
        var user = User.builder().id(id).name("Test User").build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.deleteUser(id);

        verify(userRepository).delete(user);
    }

    @Test
    void shouldNotDeleteUserNotFound() {
        var id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void shouldGetUserById() {
        var id = UUID.randomUUID();
        var user = User.builder().id(id).name("Test User").build();
        var userDto = UserDto.builder().id(id).name("Test User").build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(userDto);

        var result = userService.getUser(id);

        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    void shouldNotGetUserByIdNotFound() {
        var id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(id));
    }

    @Test
    void shouldGetUserByEmail() {
        var id = UUID.randomUUID();
        var user = User.builder().id(id).name("Test User").email("user@test.com").build();
        var userDto = UserDto.builder().id(id).name("Test User").email("user@test.com").build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(userDto);

        var result = userService.getUserByEmail(user.getEmail());

        assertThat(result.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void shouldNotGetUserByEmailNotFound() {
        var email = "example@test.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(email));
    }

    @Test
    void shouldRegisterUser() {
        var request = RegisterUserRequest.builder().name("Test User").email("example@test.com").password("NEW").build();
        var user = User.builder().name("Test User").password("NEW").email("example@test.com").build();
        var dto = UserDto.builder().id(UUID.randomUUID()).name("Test User").email("example@test.com").build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("NEW")).thenReturn("ENCODED");
        when(mapper.toEntity(request)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(dto);

        var result = userService.registerUser(request);

        assertThat(result).isSameAs(dto);
        assertThat(user.getPassword()).isEqualTo("ENCODED");
        assertThat(user.getRole()).isEqualTo(Role.USER);
        verify(userRepository).save(user);
    }

    @Test
    void shouldNotRegisterUserExistingUser() {
        var request = RegisterUserRequest.builder().name("Test User").email("example@test.com").password("NEW").build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(DuplicateUserException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldUpdateUser() {
        var id = UUID.randomUUID();
        var user = User.builder().id(id).name("Test User").email("example@test.com").build();
        var request = UpdateUserRequest.builder().name(user.getName()).email("changed@test.com").build();
        var dto = UserDto.builder().id(id).name(request.getName()).email(request.getEmail()).build();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(dto);

        var result = userService.updateUser(id, request);

        assertThat(result).isSameAs(dto);
        verify(mapper).update(request, user);
        verify(userRepository).save(user);
    }

    @Test
    void shouldNotUpdateUserNotFound() {
        var id = UUID.randomUUID();
        var user = User.builder().id(id).name("Test User").email("example@test.com").build();
        var request = UpdateUserRequest.builder().name(user.getName()).email("changed@test.com").build();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, request));
        verify(userRepository, never()).save(user);
    }

}
