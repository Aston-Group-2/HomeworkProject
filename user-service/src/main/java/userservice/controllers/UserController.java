package userservice.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import userservice.dto.CreateUserRequest;
import userservice.dto.UserDto;
import userservice.services.UserService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получить всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Получить пользователя по ID")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@Parameter(description = "ID пользователя") @PathVariable Long id) {
        var user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Создать нового пользователя")
    @ApiResponse(responseCode = "201", description = "Пользователь создан")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "409", description = "Email уже существует")
    })
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request) {
        var created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Обновить пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь обновлён")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@Parameter(description = "ID пользователя") @PathVariable Long id,
                                        @Valid @RequestBody CreateUserRequest request) {
        var updated = userService.updateUser(id, request);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить пользователя")
    @ApiResponse(responseCode = "204", description = "Пользователь удалён")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "ID пользователя") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
