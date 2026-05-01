package com.solncev.service.impl;

import com.solncev.config.properties.MailProperties;
import com.solncev.dto.CreateUserDto;
import com.solncev.dto.UserDto;
import com.solncev.entity.User;
import com.solncev.repository.UserRepository;
import com.solncev.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailProperties mailProperties;
    private final JavaMailSender mailSender;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JavaMailSender mailSender, MailProperties mailProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailProperties = mailProperties;
        this.mailSender = mailSender;
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserDto(u.getUsername())).toList();
    }

    @Override
    public void createUser(CreateUserDto createUserDto) {
        User user = new User();
        user.setUsername(createUserDto.username());
        user.setPassword(passwordEncoder.encode(createUserDto.password()));
        user.setEmail(createUserDto.email());
        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(verificationCode);
        userRepository.save(user);

        sendVerificationMail(createUserDto, verificationCode);
    }

    @Override
    @Transactional
    public boolean verify(String verificationCode) {
        return userRepository.findByVerificationCode(verificationCode)
                .map(user -> {
                    user.setEnabled(true);
                    user.setVerificationCode(null);
                    return true;
                })
                .orElse(false);
    }

    private void sendVerificationMail(CreateUserDto createUserDto, String verificationCode) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        String content = mailProperties.content();
        try {
            mimeMessageHelper.setFrom(mailProperties.from(), mailProperties.sender());
            mimeMessageHelper.setTo(createUserDto.email());
            mimeMessageHelper.setSubject(mailProperties.subject());

            content = content.replace("$name", createUserDto.username());
            content = content.replace("$url", mailProperties.baseUrl() +
                    "/verification?code=" + verificationCode);

            mimeMessageHelper.setText(content, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
