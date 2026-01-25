package ru.kuzya.orderservice.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kuzya.orderservice.dto.CreateUserRequestDto;
import ru.kuzya.orderservice.dto.UpdateUserRequest;
import ru.kuzya.orderservice.dto.UserResponseDto;
import ru.kuzya.orderservice.entity.Order;
import ru.kuzya.orderservice.entity.User;
import ru.kuzya.orderservice.mapper.UserMapper;
import ru.kuzya.orderservice.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Transactional(readOnly = true)
    public UserResponseDto getById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден по идентификатору: " + id));
        return userMapper.toDTO(user);
    }

    @Transactional
    public UserResponseDto save(CreateUserRequestDto request) {
        // Проверка уникальности email
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Пользователь c email " + request.email() + " уже существует");
        }

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    @Transactional()
    public UserResponseDto update(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден по идентификатору: " + id));
        log.info("Обновление пользователя с идентификатором: {}", id);

        userMapper.updateEntity(request, user);
        // Проверяем email на уникальность, если он изменился
//        if (request.getEmail() != null &&
//            !request.getEmail().equals(user.getEmail()) &&
//            userRepository.existsByEmail(request.getEmail())) {
//            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
//        }

//        User updatedUser = userRepository.save(user);
        log.info("UpdatedUser: {}", user);
        return userMapper.toDTO(user);
    }


    public List<UserResponseDto> getAll() {

        List<User> all = userRepository.findAll();
//        List<UserDTO> dtos = new ArrayList<>();
        List<UserResponseDto> listDTO = userMapper.toListDTO(all);
//        for (User user : all) {
//            dtos.add(userMapper.toDTO(user));
//        }
        return listDTO;
    }

}
