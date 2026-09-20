package userservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.dto.UserRequestDto;
import userservice.dto.UserResponseDto;
import userservice.exception.UserNotFoundException;
import userservice.model.User;
import userservice.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponseDto createUser(UserRequestDto request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Пользователь с email " + request.email() + " уже существует");
        }
        User user = new User(request.name(), request.email(), request.age());
        User saved = userRepository.save(user);
        return UserResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserResponseDto.fromEntity(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::fromEntity)
                .toList();
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setName(request.name());
        user.setEmail(request.email());
        user.setAge(request.age());
        User updated = userRepository.save(user);
        return UserResponseDto.fromEntity(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
