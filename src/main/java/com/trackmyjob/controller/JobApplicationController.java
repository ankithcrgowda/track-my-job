package com.trackmyjob.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trackmyjob.dto.JobApplicationRequest;
import com.trackmyjob.dto.JobApplicationResponse;
import com.trackmyjob.service.JobApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
public class JobApplicationController {

	private final JobApplicationService service;
	
	@PostMapping
	public JobApplicationResponse create(@RequestBody JobApplicationRequest request) {
		return service.createApplication(request);
	}
	
	@GetMapping
	public List<JobApplicationResponse> getMyApps() {
		return service.getMyApplications();
	}
}
