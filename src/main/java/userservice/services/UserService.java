package userservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.dto.CreateUserRequest;
import userservice.dto.UserDto;
import userservice.model.User;
import userservice.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toDto(user);
    }

    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email already in use");

        User user = new User(request.getName(), request.getEmail(), request.getAge());
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Transactional
    public UserDto updateUser(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());

        User updated = userRepository.save(user);
        return toDto(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id))
            throw new RuntimeException("User not found with id: " + id);

        userRepository.deleteById(id);
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}