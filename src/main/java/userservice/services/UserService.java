package userservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.dto.CreateUserRequest;
import userservice.dto.UserDto;
import userservice.exception.EmailAlreadyExistsException;
import userservice.exception.UserNotFoundException;
import userservice.model.User;
import userservice.notification.event.UserEvent;
import userservice.notification.kafka.UserEventProducer;
import userservice.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toDto(user);
    }

    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                request.getAge()
        );

        User saved = userRepository.save(user);

        userEventProducer.send(
                new UserEvent(
                        UserEvent.Operation.CREATED,
                        saved.getEmail()
                )
        );

        return toDto(saved);
    }

    @Transactional
    public UserDto updateUser(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());

        return toDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);

        userEventProducer.send(
                new UserEvent(
                        UserEvent.Operation.DELETED,
                        user.getEmail()
                )
        );
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