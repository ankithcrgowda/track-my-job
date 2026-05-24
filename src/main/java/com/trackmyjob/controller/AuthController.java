package com.trackmyjob.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trackmyjob.dto.AuthResponse;
import com.trackmyjob.dto.LoginRequest;
import com.trackmyjob.dto.RegisterRequest;
import com.trackmyjob.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	@PostMapping("/register")
	public String register(@RequestBody RegisterRequest request) {
		authService.register(request);
		return "User Registered Successfully";
	}
	
	@PostMapping("/login")
	public AuthResponse login(@RequestBody LoginRequest request) {
		return authService.login(request);
	}
}
