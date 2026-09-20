package userservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import userservice.dto.CreateUserRequest;
import userservice.dto.UserDto;
import userservice.model.User;
import userservice.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User sampleUser;
    private CreateUserRequest createRequest;

    @BeforeEach
    void setUp() {
        sampleUser = new User("Ivan", "ivan@test.com", 25);
        try {
            java.lang.reflect.Field field = User.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(sampleUser, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        createRequest = new CreateUserRequest();
        createRequest.setName("Ivan");
        createRequest.setEmail("ivan@test.com");
        createRequest.setAge(25);
    }

    @Test
    void getAllUsers_ShouldReturnListOfDtos() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));
        List<UserDto> result = userService.getAllUsers();
        assertEquals(1, result.size());
        assertEquals("Ivan", result.get(0).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WhenExists_ShouldReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        UserDto result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals("ivan@test.com", result.getEmail());
    }

    @Test
    void getUserById_WhenNotExists_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserById(99L));
    }

    @Test
    void createUser_WhenEmailUnique_ShouldSaveAndReturnDto() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            try {
                java.lang.reflect.Field idField = User.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.setLong(user, 1L);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return user;
        });

        UserDto result = userService.createUser(createRequest);
        assertNotNull(result.getId());
        assertEquals("Ivan", result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_WhenEmailExists_ShouldThrowException() {
        when(userRepository.existsByEmail("ivan@test.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(createRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WhenExists_ShouldUpdateFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);
        CreateUserRequest updateRequest = new CreateUserRequest();
        updateRequest.setName("Petr");
        updateRequest.setEmail("petr@test.com");
        updateRequest.setAge(30);
        UserDto result = userService.updateUser(1L, updateRequest);
        assertEquals("Petr", result.getName());
        assertEquals("petr@test.com", result.getEmail());
        assertEquals(30, result.getAge());
    }

    @Test
    void deleteUser_WhenExists_ShouldDelete() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WhenNotExists_ShouldThrowException() {
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> userService.deleteUser(99L));
    }
}