package com.trackmyjob.service.impl;

import com.trackmyjob.dto.request.JobApplicationRequest;
import com.trackmyjob.dto.response.JobApplicationResponse;
import com.trackmyjob.entity.JobApplication;
import com.trackmyjob.entity.User;
import com.trackmyjob.exception.ResourceNotFoundException;
import com.trackmyjob.repository.JobApplicationRepository;
import com.trackmyjob.repository.UserRepository;
import com.trackmyjob.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

	private final JobApplicationRepository jobApplicationRepository;
	private final UserRepository userRepository;

	/**
	 * Converts a JobApplication entity → JobApplicationResponse DTO.
	 *
	 * This is a private helper method — a "mapper".
	 * In larger projects you'd use MapStruct library for this.
	 * For our project, a simple private method is clean enough.
	 *
	 * Why a separate method? We use this mapping in every
	 * service method below — DRY principle.
	 */
	private JobApplicationResponse mapToResponse(JobApplication jobApplication) {
		return JobApplicationResponse.builder()
				.id(jobApplication.getId())
				.companyName(jobApplication.getCompanyName())
				.jobRole(jobApplication.getJobRole())
				.status(jobApplication.getStatus())
				.appliedDate(jobApplication.getAppliedDate())
				.description(jobApplication.getDescription())
				.createdAt(jobApplication.getCreatedAt())
				.updatedAt(jobApplication.getUpdatedAt())
				.build();
	}

	@Override
	public JobApplicationResponse create(Long userId, JobApplicationRequest request) {

		// Find the user who owns this application
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"User not found with id: " + userId));

		// Build the entity from the request DTO
		JobApplication application = JobApplication.builder()
				.user(user)
				.companyName(request.getCompanyName())
				.jobRole(request.getJobRole())
				.status(request.getStatus())
				.appliedDate(request.getAppliedDate())
				.description(request.getDescription())
				.build();

		JobApplication saved = jobApplicationRepository.save(application);
		return mapToResponse(saved);
	}

	@Override
	public List<JobApplicationResponse> getAllByUser(Long userId) {

		return jobApplicationRepository.findByUserId(userId)
				.stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
		// this::mapToResponse is a method reference —
		// same as writing app -> mapToResponse(app)
	}

	@Override
	public JobApplicationResponse getById(Long userId, Long applicationId) {

		JobApplication jobApplication = jobApplicationRepository
				.findByIdAndUserId(applicationId, userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Job application not found with id: " + applicationId));
		return mapToResponse(jobApplication);
	}

	@Override
	public JobApplicationResponse update(Long userId, Long applicationId, JobApplicationRequest request) {
		// Find it — scoped to this user (security check)
		JobApplication application = jobApplicationRepository
				.findByIdAndUserId(applicationId, userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Job application not found with id: " + applicationId));

		// Update only the fields the client sent
		application.setCompanyName(request.getCompanyName());
		application.setJobRole(request.getJobRole());
		application.setStatus(request.getStatus());
		application.setAppliedDate(request.getAppliedDate());
		application.setDescription(request.getDescription());

		JobApplication saved = jobApplicationRepository.save(application);
		return mapToResponse(saved);
	}

	@Override
	public void delete(Long userId, Long applicationId) {

		JobApplication application = jobApplicationRepository
				.findByIdAndUserId(applicationId, userId)
				.orElseThrow(() -> new ResourceNotFoundException(
						"Job application not found with id: " + applicationId));
		jobApplicationRepository.delete(application);
	}
}
