package com.example.jwt.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DemoController {

  @GetMapping("/public/hello")
  public String publicHello() {
    return "Hello Public!";
  }

  @GetMapping("/user/profile")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public String userProfile() {
    return "User Profile - Accessible by USER and ADMIN";
  }

  @GetMapping("/user/data")
  @PreAuthorize("hasRole('USER')")
  public String userData() {
    return "User Data - Only USER can access";
  }

  @GetMapping("/admin/dashboard")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminDashboard() {
    return "Admin Dashboard - Only ADMIN can access";
  }

  @GetMapping("/admin/users")
  @PreAuthorize("hasRole('ADMIN')")
  public String adminUsers() {
    return "Admin Users - Only ADMIN can access";
  }
}