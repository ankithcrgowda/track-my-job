package com.trackmyjob.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trackmyjob.entity.ApplicationStatus;
import com.trackmyjob.entity.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long>{
	
	List<JobApplication> findByUserId(Long userId);
	
	List<JobApplication> findByStatus(ApplicationStatus status);
	
	List<JobApplication> findByCompanyName(String companyName);
}
