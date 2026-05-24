package com.trackmyjob.service;

import java.util.List;

import com.trackmyjob.dto.CompanyDTO;

public interface CompanyService {

	CompanyDTO createCompany(CompanyDTO dto);
	
	List<CompanyDTO> getAllCompanies();
}
