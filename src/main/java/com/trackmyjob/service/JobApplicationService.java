package com.trackmyjob.service;

import com.trackmyjob.dto.request.JobApplicationRequest;
import com.trackmyjob.dto.response.JobApplicationResponse;

import java.util.List;

public interface JobApplicationService {

	JobApplicationResponse create(Long userId, JobApplicationRequest request);
	List<JobApplicationResponse> getAllByUser(Long userId);
	JobApplicationResponse getById(Long userId, Long applicationId);
	JobApplicationResponse update(Long userId, Long applicationId, JobApplicationRequest request);
	void delete(Long userId, Long applicationId);

}
