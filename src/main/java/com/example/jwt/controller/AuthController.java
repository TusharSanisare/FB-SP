package com.example.jwt.controller;

import com.example.jwt.config.JwtService;
import com.example.jwt.model.Role;
import com.example.jwt.model.User;
import com.example.jwt.dto.LoginRequest;
import com.example.jwt.dto.RegisterRequest;
import com.example.jwt.repository.UserRepository;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private static final Logger log = LoggerFactory.getLogger(AuthController.class);

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostMapping("/login")
  public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
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
    } catch (BadCredentialsException | UsernameNotFoundException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
    } catch (Exception e) {
      log.error("Unexpected error during login", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Login failed"));
    }
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      return ResponseEntity.badRequest().body(Map.of("error", "Registration failed"));
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setEmail(request.getEmail());
    user.setRoles(Set.of(Role.valueOf(request.getRole())));
    // user.setRoles(Set.of(Role.ROLE_USER));

    userRepository.save(user);

    return ResponseEntity.ok(Map.of("message", "User registered successfully"));
  }

  @PostMapping("/register-admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest request) {
    if (userRepository.existsByUsername(request.getUsername())) {
      return ResponseEntity.badRequest().body(Map.of("error", "Registration failed"));
    }

    User user = new User();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setEmail(request.getEmail());
    user.setRoles(Set.of(Role.ROLE_ADMIN));

    userRepository.save(user);

    return ResponseEntity.ok(Map.of("message", "Admin registered successfully"));
  }
}

// @RestController
// @RequestMapping("/api/auth")
// public class AuthController {
// @Autowired
// private AuthenticationManager authenticationManager;

// @Autowired
// private JwtService jwtService;

// @Autowired
// private UserRepository userRepository;

// @Autowired
// private PasswordEncoder passwordEncoder;

// @PostMapping("/login")
// public ResponseEntity<?> login(@RequestBody User request) {
// try {
// Authentication authentication = authenticationManager.authenticate(
// new UsernamePasswordAuthenticationToken(request.getUsername(),
// request.getPassword()));

// UserDetails userDetails = (UserDetails) authentication.getPrincipal();
// String token = jwtService.generateToken(userDetails);

// Map<String, Object> response = new HashMap<>();
// response.put("token", token);
// response.put("username", userDetails.getUsername());
// response.put("roles", userDetails.getAuthorities());

// return ResponseEntity.ok(response);
// } catch (Exception e) {
// return ResponseEntity.badRequest().body(Map.of("error", "Invalid
// credentials"));
// }
// }

// @PostMapping("/register")
// public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
// if (userRepository.existsByUsername(request.getUsername())) {
// return ResponseEntity.badRequest().body(Map.of("error", "Username already
// exists"));
// }

// User user = new User();
// user.setUsername(request.getUsername());
// user.setPassword(passwordEncoder.encode(request.getPassword()));
// user.setEmail(request.getEmail());
// user.setRoles(Set.of(Role.ROLE_USER));

// userRepository.save(user);

// return ResponseEntity.ok(Map.of("message", "User registered successfully"));
// }

// @PostMapping("/register-admin")
// public ResponseEntity<?> registerAdmin(@RequestBody RegisterRequest request)
// {
// if (userRepository.existsByUsername(request.getUsername())) {
// return ResponseEntity.badRequest().body(Map.of("error", "Username already
// exists"));
// }

// User user = new User();
// user.setUsername(request.getUsername());
// user.setPassword(passwordEncoder.encode(request.getPassword()));
// user.setRoles(Set.of(Role.ROLE_ADMIN));

// userRepository.save(user);

// return ResponseEntity.ok(Map.of("message", "Admin registered successfully"));
// }
// }
