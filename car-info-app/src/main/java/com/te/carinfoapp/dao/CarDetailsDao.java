package com.te.carinfoapp.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.te.carinfoapp.dto.CarDetails;

public interface CarDetailsDao extends CrudRepository<CarDetails, Integer> {
	
	CarDetails findByCompany(String company);
	CarDetails findByName(String name);
	CarDetails findByFuelType(String fuelType);
	CarDetails findById(int carId);
	List<CarDetails> findByAdminName(String name);

}
