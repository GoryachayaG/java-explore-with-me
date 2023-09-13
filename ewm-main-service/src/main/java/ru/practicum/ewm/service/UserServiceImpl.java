package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.users.UserDto;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<User> result;
        if (ids != null) {
            result = repository.findAllByIdIn(ids, page);
        } else {
            result = repository.findAll(page).getContent();
        }
        log.info("Получили список пользователей, отфильтрованных по заданным параметрам");
        return result.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User savedUser = repository.save(user);
        log.info("Создан пользователь {}", savedUser);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public void deleteUserById(Long userId) {
        repository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь" +
                        "с id = " + userId + " не найден"));
        repository.deleteById(userId);
        log.info("Удалили пользователя по id = {}", userId);
    }
}