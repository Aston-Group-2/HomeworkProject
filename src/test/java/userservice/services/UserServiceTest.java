package userservice.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import userservice.dao.UserDao;
import userservice.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDao);
        testUser = new User("Alice", "alice@example.com", 25);
    }

    @Test
    @DisplayName("Создание пользователя с валидными данными")
    void createUser_ValidData_ReturnsUser() {
        when(userDao.save(any(User.class))).thenReturn(testUser);
        User result = userService.createUser("Alice", "alice@example.com", 25);
        assertNotNull(result);
        assertEquals("Alice", result.getName());
        verify(userDao, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Создание пользователя с пустым именем должно выбросить исключение")
    void createUser_BlankName_ThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("", "email@test.com", 20));

        assertTrue(ex.getMessage().contains("Name and email cannot be empty"));
        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("Создание пользователя с отрицательным возрастом должно выбросить исключение")
    void createUser_NegativeAge_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Bob", "bob@test.com", -5));

        verify(userDao, never()).save(any());
    }

    @Test
    @DisplayName("Получение существующего пользователя по ID")
    void getUserById_ExistingUser_ReturnsOptional() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        Optional<User> result = userService.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
    }

    @Test
    @DisplayName("Получение несуществующего пользователя возвращает пустой Optional")
    void getUserById_NotFound_ReturnsEmpty() {
        when(userDao.findById(99L)).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserById(99L);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Получение списка всех пользователей")
    void getAllUsers_ReturnsList() {
        List<User> users = List.of(testUser, new User("Bob", "bob@test.com", 30));
        when(userDao.findAll()).thenReturn(users);
        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    @DisplayName("Обновление существующего пользователя")
    void updateUser_ExistingUser_UpdatesFields() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDao.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User updated = userService.updateUser(1L, "New Name", "new@email.com", 40);
        assertEquals("New Name", updated.getName());
        assertEquals("new@email.com", updated.getEmail());
        assertEquals(40, updated.getAge());
        verify(userDao, times(1)).update(any(User.class));
    }

    @Test
    @DisplayName("Обновление несуществующего пользователя выбрасывает RuntimeException")
    void updateUser_NotFound_ThrowsException() {
        when(userDao.findById(99L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateUser(99L, "Name", "email", 20));

        assertTrue(ex.getMessage().contains("User not found with id: 99"));
        verify(userDao, never()).update(any());
    }

    @Test
    @DisplayName("Удаление пользователя по ID")
    void deleteUser_CallsDaoDelete() {
        doNothing().when(userDao).delete(1L);
        userService.deleteUser(1L);
        verify(userDao, times(1)).delete(1L);
    }
}