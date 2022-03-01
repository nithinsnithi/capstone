package com.te.carinfoapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.te.carinfoapp.dto.AdminDetails;
import com.te.carinfoapp.dto.AdminRequest;
import com.te.carinfoapp.dto.AdminResponse;
import com.te.carinfoapp.dto.CarDetails;
import com.te.carinfoapp.dto.CarDetailsResponse;
import com.te.carinfoapp.service.AdminService;
import com.te.carinfoapp.util.JwtUtil;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/admin")
public class AdminController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AdminService adminService;

	@PostMapping("/loginAuth")
	public ResponseEntity<?> createLoginAuthenticationToken(@RequestBody AdminRequest admin) {
		if (adminService.checkIfUsernameExists(admin.getUsername())) {
			try {
				authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(admin.getUsername(), admin.getPassword()));
			} catch (AuthenticationException e) {
				return ResponseEntity.ok(new AdminResponse(true, "Invalid Username or Password", null,null));
			}
			UserDetails userDetails = userDetailsService.loadUserByUsername(admin.getUsername());
			AdminDetails adminDetails = adminService.adminDetails(userDetails.getUsername());
			String jwtToken = jwtUtil.generateToken(userDetails);
			return ResponseEntity.ok(new AdminResponse(false, "Authentication Success", jwtToken,adminDetails.getRole()));
		} else {
			return ResponseEntity.ok(new AdminResponse(true, "Username not Found, Please Signup", null,null));
		}
	}

	@PostMapping("/signupAuth")
	public ResponseEntity<?> createSignupAuthenticationToken(@RequestBody AdminDetails adminDetails) {

		AdminDetails signupData = null;
		try {
			signupData = adminService.saveSignupData(adminDetails);
		} catch (DataIntegrityViolationException exception) {
			return ResponseEntity.ok(new AdminResponse(true, "Username Already Exists",null, signupData.getRole()));
		}
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signupData.getUsername(), signupData.getPassword()));

		UserDetails userDetails = userDetailsService.loadUserByUsername(signupData.getUsername());
		AdminDetails adminDetails1 = adminService.adminDetails(userDetails.getUsername());
		String jwtToken = jwtUtil.generateToken(userDetails);

		return ResponseEntity.ok(new AdminResponse(false, "Signup Success", jwtToken,adminDetails1.getRole()));

	}

	@GetMapping("/car/info")
	public ResponseEntity<?> getAllCarDetails(HttpServletRequest  request) {

		try {
			List<CarDetails> cars = adminService.getAllCarDetails(request);
			return ResponseEntity.ok(new CarDetailsResponse(false, "success", cars));
		} catch (Exception e) {
			return ResponseEntity.ok(new CarDetailsResponse(true, "something went wrong", null));
		}
	}

	@PostMapping("/car/info")
	public ResponseEntity<?> addCarDetails(@RequestBody CarDetails carDetails, HttpServletRequest request) {

		try {
			adminService.addCarDetails(carDetails, request);
			return ResponseEntity.ok(new CarDetailsResponse(false, "Car Details Added Successfully", null));
		} catch (Exception e) {
			return ResponseEntity.ok(new CarDetailsResponse(true, "Oops something went wrong", null));
		}

	}

	@PutMapping("/car/info/{carId}")
	public ResponseEntity<?> updateCarDetails(@RequestBody CarDetails carDetails, HttpServletRequest request,
			@PathVariable int carId) {

		try {
			carDetails.setId(carId);
			adminService.updateCarDetails(carDetails, request);
			return ResponseEntity.ok(new CarDetailsResponse(false, "Car Details Updated Successfully", null));
		} catch (Exception e) {
			return ResponseEntity.ok(new CarDetailsResponse(false, "Oops something went wrong", null));
		}

	}

	@DeleteMapping("/car/info/{carId}")
	public ResponseEntity<?> deleteCarDetails(@PathVariable int carId) {
		try {
			adminService.deleteCarDetails(carId);
			return ResponseEntity.ok(new CarDetailsResponse(false, "Car Details Deleted Successfully", null));
		} catch (Exception e) {
			return ResponseEntity.ok(new CarDetailsResponse(true, "Opps something went wrong", null));
		}

	}

}
