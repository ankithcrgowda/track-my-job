package com.trackmyjob.service;

import com.trackmyjob.dto.AuthResponse;
import com.trackmyjob.dto.LoginRequest;
import com.trackmyjob.dto.RegisterRequest;

public interface AuthService {

	void register(RegisterRequest request);
	AuthResponse login(LoginRequest request);
}
