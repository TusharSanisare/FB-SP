package com.example.jwt.controller;

import com.example.jwt.config.JwtService;
import com.example.jwt.model.Role;
import com.example.jwt.model.User;
import com.example.jwt.dto.RegisterRequest;
import com.example.jwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody User request) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      String token = jwtService.generateToken(userDetails);

      Map<String, Object> response = new HashMap<>();
      response.put("token", token);
      response.put("username", userDetails.getUsername());
      response.put("roles", userDetails.getAuthorities());

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setEmail(request.getEmail());
    user.setRoles(Set.of(Role.ROLE_USER));

    userRepository.save(user);

    return ResponseEntity.ok(Map.of("message", "User registered successfully"));
  }

  @PostMapping("/register-admin")
  public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRoles(Set.of(Role.ROLE_ADMIN));

    userRepository.save(user);

    return ResponseEntity.ok(Map.of("message", "Admin registered successfully"));
  }
}
