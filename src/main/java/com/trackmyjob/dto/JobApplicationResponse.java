package com.trackmyjob.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobApplicationResponse {

	private Long id;
	private String jobTitle;
	private String companyName;
	private String status;
}
