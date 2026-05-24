package com.trackmyjob.service.impl;

import javax.management.RuntimeErrorException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.trackmyjob.dto.AuthResponse;
import com.trackmyjob.dto.LoginRequest;
import com.trackmyjob.dto.RegisterRequest;
import com.trackmyjob.entity.Role;
import com.trackmyjob.entity.RoleEnum;
import com.trackmyjob.entity.User;
import com.trackmyjob.repository.RoleRepository;
import com.trackmyjob.repository.UserRepository;
import com.trackmyjob.security.JwtUtil;
import com.trackmyjob.service.AuthService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
	
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	
	@Override
	public void register(RegisterRequest request) {

		Role role = roleRepository.findByName(RoleEnum.USER.name())
					.orElseThrow();
		
		User user = User.builder()
					.name(request.getName())
					.email(request.getEmail())
					.password(passwordEncoder.encode(request.getPassword()))
					.role(role)
					.build();
		
		userRepository.save(user);
	}
	
	
	@Override
	public AuthResponse login(LoginRequest request) {
		
		User user = userRepository.findByEmail(request.getEmail())
					.orElseThrow();
		if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("Invalid Credentials");
		}
		
		String token = jwtUtil.generateToken(user.getEmail());
		
		return new AuthResponse(token);
	}
}













