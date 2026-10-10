package userservice.services;

import common.event.OperationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import userservice.controllers.UserController;
import userservice.dto.CreateUserRequest;
import userservice.dto.UserDto;
import userservice.event.UserChangedEvent;
import userservice.exception.EmailAlreadyExistsException;
import userservice.exception.UserNotFoundException;
import userservice.model.User;
import userservice.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CollectionModel<EntityModel<UserDto>> getAllUsers() {
        List<EntityModel<UserDto>> users = userRepository.findAll().stream()
                .map(user -> {
                    UserDto dto = toDto(user);
                    return EntityModel.of(dto,
                            linkTo(methodOn(UserController.class).getUserById(user.getId())).withRel("self"),
                            linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
                })
                .collect(Collectors.toList());
        return CollectionModel.of(users, linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }

    public EntityModel<UserDto> getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        UserDto dto = toDto(user);
        return EntityModel.of(dto,
                linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"),
                linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));
    }

    @Transactional
    public EntityModel<UserDto> createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new EmailAlreadyExistsException(request.getEmail());

        User user = new User(request.getName(), request.getEmail(), request.getAge());
        User saved = userRepository.save(user);

        eventPublisher.publishEvent(new UserChangedEvent(saved.getEmail(), OperationType.CREATE));

        UserDto dto = toDto(saved);
        return EntityModel.of(dto,
                linkTo(methodOn(UserController.class).getUserById(saved.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
    }

    @Transactional
    public EntityModel<UserDto> updateUser(Long id, CreateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());

        UserDto dto = toDto(user);
        return EntityModel.of(dto,
                linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(id);

        eventPublisher.publishEvent(new UserChangedEvent(user.getEmail(), OperationType.DELETE));
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt());
    }
}
