package com.solncev.controller;

import com.solncev.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationController {

    private final UserService userService;

    public VerificationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/verification")
    public ResponseEntity<String> verify(@RequestParam("code") String code) {
        return userService.verify(code)
                ? ResponseEntity.ok("Account verified")
                : ResponseEntity.status(404).body("Invalid verification code");
    }
}
