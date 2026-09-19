package userservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import userservice.repository.UserRepository;
import userservice.model.User;

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

    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);
        testUser = new User("Vasya", "vasya@example.com", 25);
    }

    @Test
    void createUser_ValidData_ReturnsUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        User result = userService.createUser("Vasya", "vasya@example.com", 25);
        assertNotNull(result);
        assertEquals("Vasya", result.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_BlankName_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("", "email@example.com", 20));

        assertTrue(ex.getMessage().contains("Name and email cannot be empty"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_NegativeAge_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Vasya", "vasya@example.com", -5));

        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_ExistingUser_ReturnsOptional() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        Optional<User> result = userService.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals("Vasya", result.get().getName());
    }

    @Test
    void getUserById_NotFound_ReturnsEmpty() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserById(99L);
        assertFalse(result.isPresent());
    }

    @Test
    void getAllUsers_ReturnsList() {
        List<User> users = List.of(testUser, new User("Vasya", "vasya@example.com", 30));
        when(userRepository.findAll()).thenReturn(users);
        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_ExistingUser_UpdatesFields() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User updated = userService.updateUser(1L, "New Name", "new@email.com", 40);
        assertEquals("New Name", updated.getName());
        assertEquals("new@email.com", updated.getEmail());
        assertEquals(40, updated.getAge());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateUser(99L, "Name", "email", 20));

        assertTrue(ex.getMessage().contains("User not found with id: 99"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_CallsDaoDelete() {
        doNothing().when(userRepository).deleteById(1L);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
}