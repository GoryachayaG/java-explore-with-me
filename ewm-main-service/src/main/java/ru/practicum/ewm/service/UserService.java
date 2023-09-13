package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.users.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    UserDto createUser(UserDto userDto);

    void deleteUserById(Long userId);
}