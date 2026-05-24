package com.trackmyjob.service;

import java.util.List;

import com.trackmyjob.dto.JobApplicationRequest;
import com.trackmyjob.dto.JobApplicationResponse;

public interface JobApplicationService {
	JobApplicationResponse createApplication(JobApplicationRequest request);
	List<JobApplicationResponse> getMyApplications();
}
