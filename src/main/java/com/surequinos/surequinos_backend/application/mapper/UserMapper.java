package com.surequinos.surequinos_backend.application.mapper;

import com.surequinos.surequinos_backend.application.dto.UserDto;
import com.surequinos.surequinos_backend.application.dto.request.CreateUserRequest;
import com.surequinos.surequinos_backend.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * Mapper para conversiones entre User entity y DTOs
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    /**
     * Convierte una entidad User a UserDto
     */
    UserDto toDto(User user);

    /**
     * Convierte una lista de entidades User a lista de UserDto
     */
    List<UserDto> toDtoList(List<User> users);

    /**
     * Convierte un CreateUserRequest a entidad User
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    User toEntity(CreateUserRequest request);
}

