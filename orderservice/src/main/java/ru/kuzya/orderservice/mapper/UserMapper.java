package ru.kuzya.orderservice.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.kuzya.orderservice.dto.CreateUserRequestDto;
import ru.kuzya.orderservice.dto.UpdateUserRequest;
import ru.kuzya.orderservice.dto.UserResponseDto;
import ru.kuzya.orderservice.entity.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDto toDTO(User user);

    List<UserResponseDto> toListDTO(List<User> user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(CreateUserRequestDto request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);
}