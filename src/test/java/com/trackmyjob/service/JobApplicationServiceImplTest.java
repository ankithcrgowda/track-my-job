package com.trackmyjob.service;

import com.trackmyjob.dto.request.JobApplicationRequest;
import com.trackmyjob.dto.response.JobApplicationResponse;
import com.trackmyjob.entity.ApplicationStatus;
import com.trackmyjob.entity.JobApplication;
import com.trackmyjob.entity.User;
import com.trackmyjob.exception.ResourceNotFoundException;
import com.trackmyjob.repository.JobApplicationRepository;
import com.trackmyjob.repository.UserRepository;
import com.trackmyjob.service.impl.JobApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobApplicationService Unit Tests")
class JobApplicationServiceImplTest {

	@Mock
	private JobApplicationRepository jobApplicationRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private JobApplicationServiceImpl jobApplicationService;

	private User testUser;
	private JobApplication testApplication;
	private JobApplicationRequest testRequest;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
				.name("Ankith CR")
				.email("ankith@example.com")
				.password("hashed")
				.build();

		testRequest = new JobApplicationRequest();
		testRequest.setCompanyName("IBM India");
		testRequest.setJobRole("Full Stack Developer");
		testRequest.setStatus(ApplicationStatus.APPLIED);
		testRequest.setAppliedDate(LocalDate.of(2026, 5, 28));
		testRequest.setDescription("Applied via referral");

		testApplication = new JobApplication();
		testApplication.setUser(testUser);
		testApplication.setCompanyName("IBM India");
		testApplication.setJobRole("Full Stack Developer");
		testApplication.setStatus(ApplicationStatus.APPLIED);
		testApplication.setAppliedDate(LocalDate.of(2026, 5, 28));
		testApplication.setDescription("Applied via referral");
	}

	@Test
	@DisplayName("Create — success returns JobApplicationResponse")
	void create_Success_ReturnsJobApplicationResponse() {
		when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
		when(jobApplicationRepository.save(any(JobApplication.class)))
				.thenReturn(testApplication);

		JobApplicationResponse response =
				jobApplicationService.create(1L, testRequest);

		assertThat(response).isNotNull();
		assertThat(response.getCompanyName()).isEqualTo("IBM India");
		assertThat(response.getJobRole()).isEqualTo("Full Stack Developer");
		assertThat(response.getStatus()).isEqualTo(ApplicationStatus.APPLIED);

		verify(jobApplicationRepository).save(any(JobApplication.class));
	}

	@Test
	@DisplayName("Create — user not found throws ResourceNotFoundException")
	void create_UserNotFound_ThrowsResourceNotFoundException() {
		when(userRepository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() ->
				jobApplicationService.create(99L, testRequest))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("99");

		verify(jobApplicationRepository, never()).save(any());
	}

	@Test
	@DisplayName("GetAll — returns list of applications for user")
	void getAllByUser_ReturnsListOfApplications() {
		when(jobApplicationRepository.findByUserId(1L))
				.thenReturn(List.of(testApplication));

		List<JobApplicationResponse> responses =
				jobApplicationService.getAllByUser(1L);

		assertThat(responses).hasSize(1);
		assertThat(responses.get(0).getCompanyName()).isEqualTo("IBM India");
	}

	@Test
	@DisplayName("GetAll — returns empty list when no applications")
	void getAllByUser_NoApplications_ReturnsEmptyList() {
		when(jobApplicationRepository.findByUserId(1L))
				.thenReturn(List.of());

		List<JobApplicationResponse> responses =
				jobApplicationService.getAllByUser(1L);

		assertThat(responses).isEmpty();
	}

	@Test
	@DisplayName("Delete — application not found throws ResourceNotFoundException")
	void delete_ApplicationNotFound_ThrowsResourceNotFoundException() {
		when(jobApplicationRepository.findByIdAndUserId(99L, 1L))
				.thenReturn(Optional.empty());

		assertThatThrownBy(() ->
				jobApplicationService.delete(1L, 99L))
				.isInstanceOf(ResourceNotFoundException.class);

		verify(jobApplicationRepository, never()).delete(any());
	}

	@Test
	@DisplayName("Update — success returns updated response")
	void update_Success_ReturnsUpdatedResponse() {
		JobApplicationRequest updateRequest = new JobApplicationRequest();
		updateRequest.setCompanyName("Google");
		updateRequest.setJobRole("Backend Engineer");
		updateRequest.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
		updateRequest.setAppliedDate(LocalDate.of(2026, 6, 1));
		updateRequest.setDescription("Interview on June 10");

		JobApplication updatedApplication = new JobApplication();
		updatedApplication.setUser(testUser);
		updatedApplication.setCompanyName("Google");
		updatedApplication.setJobRole("Backend Engineer");
		updatedApplication.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
		updatedApplication.setAppliedDate(LocalDate.of(2026, 6, 1));
		updatedApplication.setDescription("Interview on June 10");

		when(jobApplicationRepository.findByIdAndUserId(1L, 1L))
				.thenReturn(Optional.of(testApplication));
		when(jobApplicationRepository.save(any(JobApplication.class)))
				.thenReturn(updatedApplication);

		JobApplicationResponse response =
				jobApplicationService.update(1L, 1L, updateRequest);

		assertThat(response.getCompanyName()).isEqualTo("Google");
		assertThat(response.getStatus())
				.isEqualTo(ApplicationStatus.INTERVIEW_SCHEDULED);
	}
}