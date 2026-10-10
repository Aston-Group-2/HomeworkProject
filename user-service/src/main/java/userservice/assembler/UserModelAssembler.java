package userservice.assembler;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import userservice.controllers.UserController;
import userservice.dto.UserDto;

import java.util.stream.StreamSupport;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<UserDto, EntityModel<UserDto>> {

    @Override
    public EntityModel<UserDto> toModel(UserDto userDto) {

        return EntityModel.of(userDto,
                linkTo(methodOn(UserController.class).getUserById(userDto.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"),
                linkTo(methodOn(UserController.class).deleteUser(userDto.getId())).withRel("delete"));
    }

    public CollectionModel<EntityModel<UserDto>> toCollectionModel(Iterable<UserDto> entities) {

        var entityModels = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(entityModels, linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }
}