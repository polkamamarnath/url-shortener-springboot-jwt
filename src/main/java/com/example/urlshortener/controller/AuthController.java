package com.example.urlshortener.controller;

import com.example.urlshortener.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.urlshortener.user.User;
import com.example.urlshortener.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    @PostMapping("/register")
    public String register(
            @RequestBody Map<String,String> request) {

        String username = request.get("username");
        String password = request.get("password");

        if(userRepository.findByUsername(username).isPresent()) {
            return "Username already exists";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(
                encoder.encode(password)
        );

        userRepository.save(user);

        return "User Registered Successfully";
    }
    @PostMapping("/login")
    public String login(
            @RequestBody Map<String, String> request) {

        String username = request.get("username");
        String password = request.get("password");

        User user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {
            return "User not found";
        }

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        if (!encoder.matches(password, user.getPassword())) {
            return "Invalid Password";
        }

        return jwtUtil.generateToken(username);
    }
}