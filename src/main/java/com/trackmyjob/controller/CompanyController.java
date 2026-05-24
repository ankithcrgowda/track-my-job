package com.trackmyjob.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trackmyjob.dto.CompanyDTO;
import com.trackmyjob.service.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {

	private final CompanyService service;

	@PostMapping
	public CompanyDTO create(@RequestBody CompanyDTO dto) {
		return service.createCompany(dto);
	}
	
	@GetMapping
	public List<CompanyDTO> getAll() {
		return service.getAllCompanies();
	}
}
