package edu.studentattendance.dto;

import edu.studentattendance.entity.Role;

public record LoginResponse(String token, String username, Role role) {}
