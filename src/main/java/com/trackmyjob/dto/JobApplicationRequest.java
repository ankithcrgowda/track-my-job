package com.trackmyjob.dto;

import lombok.Data;

@Data
public class JobApplicationRequest {

	private String jobTitle;
	private String description;
	private String salaryRange;
	private Long companyId;
}
