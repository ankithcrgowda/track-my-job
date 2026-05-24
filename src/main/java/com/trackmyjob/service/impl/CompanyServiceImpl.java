package com.trackmyjob.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.trackmyjob.dto.CompanyDTO;
import com.trackmyjob.entity.Company;
import com.trackmyjob.repository.CompanyRepository;
import com.trackmyjob.service.CompanyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService{

	private final CompanyRepository companyRepository;
	
	@Override
	public CompanyDTO createCompany(CompanyDTO dto) {
		
		companyRepository.findByName(dto.getName())
						.ifPresent(c -> {throw new RuntimeException("Company Already Exists!...");});
		
		Company company = Company.builder()
						.name(dto.getName())
						.website(dto.getWebsite())
						.location(dto.getLocation())
						.build();
		
		Company saved = companyRepository.save(company);
		
		dto.setId(saved.getId());
		return dto;
		
	}
	
	@Override
	public List<CompanyDTO> getAllCompanies() {

		return companyRepository.findAll().stream().map(c -> {
			CompanyDTO dto = new CompanyDTO();
			dto.setId(c.getId());
			dto.setName(c.getName());
			dto.setWebsite(c.getWebsite());
			dto.setLocation(c.getLocation());
			return dto;
			
		}).toList();
	}
}
