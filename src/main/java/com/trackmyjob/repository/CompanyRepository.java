package com.trackmyjob.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.trackmyjob.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
	
	Optional<Company> findByName(String name);
}
