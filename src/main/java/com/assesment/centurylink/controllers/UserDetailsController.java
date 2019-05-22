package com.assesment.centurylink.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assesment.centurylink.dto.RepositoryDetailsResponse;
import com.assesment.centurylink.dto.UserDetailsResponse;
import com.assesment.centurylink.service.UserDetailsService;

@RestController
public class UserDetailsController {

	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * Method to fetch the user details based on the user id
	 *
	 * @param userId
	 * @return
	 */
	@GetMapping(value = "/user-details")
	public UserDetailsResponse fetchUserDetails(String userId) {

		return userDetailsService.fetchUserDetails(userId);
	}

	/**
	 * Method to fetch the repository details based on the user id
	 *
	 * @param userId
	 * @return
	 */
	@GetMapping(value = "/repository-details")
	public RepositoryDetailsResponse fetchRepositoryDetails(String userId) {

		return userDetailsService.fetchRepositoryDetails(userId);
	}
}
