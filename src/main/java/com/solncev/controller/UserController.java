package com.solncev.controller;

import com.solncev.dto.CreateUserDto;
import com.solncev.dto.UserDto;
import com.solncev.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/users")
    public String createUser(@RequestBody CreateUserDto createUserDto) {
        userService.createUser(createUserDto);
        return "success_sign_up";
    }

    @ResponseBody
    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

}
