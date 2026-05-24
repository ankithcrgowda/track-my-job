package com.trackmyjob.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.trackmyjob.controller.AuthController;
import com.trackmyjob.dto.JobApplicationRequest;
import com.trackmyjob.dto.JobApplicationResponse;
import com.trackmyjob.entity.ApplicationStatus;
import com.trackmyjob.entity.Company;
import com.trackmyjob.entity.JobApplication;
import com.trackmyjob.entity.User;
import com.trackmyjob.repository.CompanyRepository;
import com.trackmyjob.repository.JobApplicationRepository;
import com.trackmyjob.repository.UserRepository;
import com.trackmyjob.service.JobApplicationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService{

	private final JobApplicationRepository applicationRepository;
	private final CompanyRepository companyRepository;
	private final UserRepository userRepository;

	@Override
	public JobApplicationResponse createApplication(JobApplicationRequest request) {

		String email = SecurityContextHolder.getContext()
						.getAuthentication().getName();
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found!..."));
		
		Company company = companyRepository.findById(request.getCompanyId())
				.orElseThrow(() -> new RuntimeException("Company not found!.."));
		
		JobApplication application = JobApplication.builder()
								.jobTitle(request.getJobTitle())
								.description(request.getDescription())
								.salaryRange(request.getSalaryRange())
								.status(ApplicationStatus.APPLIED)
								.appliedDate(LocalDate.now())
								.user(user)
								.company(company)
								.build();
		
		JobApplication saved = applicationRepository.save(application);
		
		return JobApplicationResponse.builder()
				.id(saved.getId())
				.jobTitle(saved.getJobTitle())
				.companyName(saved.getCompany().getName())
				.status(saved.getStatus().name())
				.build();
	}
	
	@Override
	public List<JobApplicationResponse> getMyApplications() {
		
		String email = SecurityContextHolder.getContext()
					.getAuthentication().getName();
		
		User user = userRepository.findByEmail(email)
		.orElseThrow(() -> new RuntimeException("No user found"));
		
		return applicationRepository.findByUserId(user.getId())
				.stream()
				.map(app -> JobApplicationResponse.builder()
						.id(app.getId())
						.jobTitle(app.getJobTitle())
						.companyName(app.getCompany().getName())
						.status(app.getStatus().name())
						.build())
				.toList();
	}
}













