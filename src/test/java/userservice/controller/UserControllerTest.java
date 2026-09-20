package userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import userservice.dto.UserRequestDto;
import userservice.dto.UserResponseDto;
import userservice.exception.GlobalExceptionHandler;
import userservice.exception.UserNotFoundException;
import userservice.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = { UserController.class, GlobalExceptionHandler.class })
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private UserService userService;

        // Смешные тестовые данные вместо скучных test@mail.ru
        private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2024, 1, 1, 12, 0);
        private static final UserResponseDto HOMER = new UserResponseDto(
                        1L, "Гомер Симпсон", "homer@springfield.doh", 39, FIXED_TIME);
        private static final UserResponseDto BENDER = new UserResponseDto(
                        2L, "Бендер Оглы", "bender@robot.hell", 4, FIXED_TIME);

        @Test
        @DisplayName("POST /api/users — создаёт пользователя (201)")
        void createUser_returnsCreated() throws Exception {
                UserRequestDto request = new UserRequestDto("Гомер Симпсон", "homer@springfield.doh", 39);
                given(userService.createUser(any(UserRequestDto.class))).willReturn(HOMER);

                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(header().string("Location", "/api/users/1"))
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.name").value("Гомер Симпсон"))
                                .andExpect(jsonPath("$.email").value("homer@springfield.doh"))
                                .andExpect(jsonPath("$.age").value(39));
        }

        @Test
        @DisplayName("POST /api/users — битые данные (400)")
        void createUser_invalidBody_returnsBadRequest() throws Exception {
                UserRequestDto request = new UserRequestDto("", "не-емейл", -5);

                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET /api/users/{id} — возвращает пользователя (200)")
        void getUserById_returnsUser() throws Exception {
                given(userService.getUserById(1L)).willReturn(HOMER);

                mockMvc.perform(get("/api/users/{id}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.name").value("Гомер Симпсон"))
                                .andExpect(jsonPath("$.email").value("homer@springfield.doh"));
        }

        @Test
        @DisplayName("GET /api/users/{id} — несуществующий пользователь (404)")
        void getUserById_notFound_returnsNotFound() throws Exception {
                given(userService.getUserById(999L)).willThrow(new UserNotFoundException(999L));

                mockMvc.perform(get("/api/users/{id}", 999L))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Пользователь с id 999 не найден"));
        }

        @Test
        @DisplayName("GET /api/users — возвращает список всех пользователей")
        void getAllUsers_returnsList() throws Exception {
                given(userService.getAllUsers()).willReturn(List.of(HOMER, BENDER));

                mockMvc.perform(get("/api/users"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].name").value("Гомер Симпсон"))
                                .andExpect(jsonPath("$[1].name").value("Бендер Оглы"));
        }

        @Test
        @DisplayName("GET /api/users — пустой список (200) и []")
        void getAllUsers_empty_returnsEmptyList() throws Exception {
                given(userService.getAllUsers()).willReturn(List.of());

                mockMvc.perform(get("/api/users"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("PUT /api/users/{id} — обновляет пользователя (200)")
        void updateUser_returnsUpdatedUser() throws Exception {
                UserRequestDto request = new UserRequestDto("Бендер Оглы", "bender@robot.hell", 4);
                given(userService.updateUser(eq(2L), any(UserRequestDto.class))).willReturn(BENDER);

                mockMvc.perform(put("/api/users/{id}", 2L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(2L))
                                .andExpect(jsonPath("$.name").value("Бендер Оглы"))
                                .andExpect(jsonPath("$.age").value(4));
        }

        @Test
        @DisplayName("PUT /api/users/{id} — несуществующий пользователь (404)")
        void updateUser_notFound_returnsNotFound() throws Exception {
                UserRequestDto request = new UserRequestDto("Бендер Оглы", "bender@robot.hell", 4);
                given(userService.updateUser(eq(999L), any(UserRequestDto.class)))
                                .willThrow(new UserNotFoundException(999L));

                mockMvc.perform(put("/api/users/{id}", 999L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Пользователь с id 999 не найден"));
        }

        @Test
        @DisplayName("DELETE /api/users/{id} — удаляет пользователя (204)")
        void deleteUser_returnsNoContent() throws Exception {
                mockMvc.perform(delete("/api/users/{id}", 1L))
                                .andExpect(status().isNoContent());

                verify(userService).deleteUser(1L);
        }

        @Test
        @DisplayName("DELETE /api/users/{id} — несуществующий пользователь (404)")
        void deleteUser_notFound_returnsNotFound() throws Exception {
                doThrow(new UserNotFoundException(999L)).when(userService).deleteUser(999L);

                mockMvc.perform(delete("/api/users/{id}", 999L))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Пользователь с id 999 не найден"));
        }
}
