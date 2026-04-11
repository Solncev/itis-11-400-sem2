package com.solncev.service;

import com.solncev.dto.CreateUserDto;
import com.solncev.dto.UserDto;

import java.util.List;

public interface UserService {
    void createUser(CreateUserDto createUserDto);
    List<UserDto> getUsers();
}
