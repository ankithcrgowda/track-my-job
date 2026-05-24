package com.trackmyjob.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.trackmyjob.entity.Role;
import com.trackmyjob.entity.RoleEnum;
import com.trackmyjob.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

	private final RoleRepository roleRepository;

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
        for (RoleEnum roleEnum : RoleEnum.values()) {
            roleRepository.findByName(roleEnum.name())
                    .orElseGet(() -> roleRepository.save(
                            Role.builder()
                                    .name(roleEnum.name())
                                    .build()
                    ));
        }
	}

}
