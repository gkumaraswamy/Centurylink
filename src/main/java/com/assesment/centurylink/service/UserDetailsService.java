package com.assesment.centurylink.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.assesment.centurylink.dto.ExceptionPayload;
import com.assesment.centurylink.dto.RepositoryDetails;
import com.assesment.centurylink.dto.RepositoryDetailsResponse;
import com.assesment.centurylink.dto.StargazersDto;
import com.assesment.centurylink.dto.StargazersResponse;
import com.assesment.centurylink.dto.UserDetailsDto;
import com.assesment.centurylink.dto.UserDetailsResponse;
import com.assesment.centurylink.utils.GitAssesmentException;

@Service
public class UserDetailsService {

	@Value("${git.user.name}")
	private String userName;

	@Value("${git.password}")
	private String password;

	/**
	 * Method to fetch the user details based on the user id
	 *
	 * @param userId
	 * @return
	 */
	public UserDetailsResponse fetchUserDetails(String userId) {

		final List<String> followers = getFollowersForUser(userId);
		final UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
		userDetailsResponse.setUserId(userId);
		final List<UserDetailsResponse> responseList = getFollowersList(followers);
		userDetailsResponse.setFollowers(responseList);
		return userDetailsResponse;

	}

	/**
	 * Method to fetch the followers for a user based on the userId
	 *
	 *
	 */
	private List<String> getFollowersForUser(String userId) {
		final HttpHeaders headers = getHeaders();
		final HttpEntity<String> request = new HttpEntity<>(headers);
		final RestTemplate restTemplate = new RestTemplate();
		final ResponseEntity<UserDetailsDto> userEntity = restTemplate
				.exchange("https://api.github.com/users/" + userId, HttpMethod.GET, request, UserDetailsDto.class);
		final UserDetailsDto user = userEntity.getBody();
		if (!Optional.ofNullable(user).isPresent()) {
			throw new GitAssesmentException(HttpStatus.BAD_REQUEST, new ExceptionPayload("Invalid user Id provided"));
		}
		final String followersUrl = user.getFollowers_url();

		final ResponseEntity<List<UserDetailsDto>> rateResponse = restTemplate.exchange(followersUrl, HttpMethod.GET,
				request, new ParameterizedTypeReference<List<UserDetailsDto>>() {
				});
		final List<UserDetailsDto> list = rateResponse.getBody();
		return list.parallelStream().limit(5).map(UserDetailsDto::getLogin).collect(Collectors.toList());

	}

	/**
	 * Method to fetch the followers of followers
	 *
	 */
	private List<UserDetailsResponse> getFollowersList(final List<String> followers) {
		final List<UserDetailsResponse> responseList = new ArrayList<>();
		for (final String s : followers) {
			final List<String> followers2 = getFollowersForUser(s);

			final List<UserDetailsResponse> responseList2 = new ArrayList<>();
			for (final String s2 : followers2) {
				final UserDetailsResponse userDetailsResponse2 = new UserDetailsResponse();
				userDetailsResponse2.setUserId(s2);
				responseList2.add(userDetailsResponse2);
			}

			final UserDetailsResponse userDetailsResponse2 = new UserDetailsResponse();
			userDetailsResponse2.setUserId(s);
			userDetailsResponse2.setFollowers(responseList2);
			responseList.add(userDetailsResponse2);
		}
		return responseList;
	}

	/**
	 * Method to set the headers for the rest template request
	 *
	 *
	 */
	private HttpHeaders getHeaders() {
		final String plainCreds = userName + ":" + password;
		final byte[] plainCredsBytes = plainCreds.getBytes();
		final byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		final String base64Creds = new String(base64CredsBytes);

		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);

		return headers;
	}

	/**
	 * Method to fetch the repository details based on the user id
	 * 
	 * @param userId
	 * @return
	 */
	public RepositoryDetailsResponse fetchRepositoryDetails(String userId) {
		final List<String> listRepositories = getRepositoriesForUser(userId);
		final RepositoryDetailsResponse repositoryDetailsResponse = new RepositoryDetailsResponse();
		repositoryDetailsResponse.setUserId(userId);
		final List<StargazersDto> repositoriesList = new ArrayList<>();
		for (final String repository : listRepositories) {
			final StargazersDto stargazersDto = new StargazersDto();
			stargazersDto.setRepositoryName(repository);
			final String stargazersUrl = "https://api.github.com/repos/" + userId + "/" + repository + "/stargazers";
			final HttpHeaders headers = getHeaders();
			final HttpEntity<String> request = new HttpEntity<>(headers);
			final RestTemplate restTemplate = new RestTemplate();
			final ResponseEntity<List<StargazersResponse>> rateResponse = restTemplate.exchange(stargazersUrl,
					HttpMethod.GET, request, new ParameterizedTypeReference<List<StargazersResponse>>() {
					});
			final List<StargazersResponse> list = rateResponse.getBody();
			final List<String> stragazersList = list.parallelStream().limit(3).map(StargazersResponse::getLogin)
					.collect(Collectors.toList());
			stargazersDto.setStargazersList(stragazersList);
			repositoriesList.add(stargazersDto);
		}
		repositoryDetailsResponse.setRepositoriesList(repositoriesList);
		return repositoryDetailsResponse;
	}

	/**
	 * Method to fetch the repositories for a user based on the userId
	 *
	 *
	 */
	private List<String> getRepositoriesForUser(String userId) {
		final HttpHeaders headers = getHeaders();
		final HttpEntity<String> request = new HttpEntity<>(headers);
		final RestTemplate restTemplate = new RestTemplate();
		final ResponseEntity<UserDetailsDto> userEntity = restTemplate
				.exchange("https://api.github.com/users/" + userId, HttpMethod.GET, request, UserDetailsDto.class);
		final UserDetailsDto user = userEntity.getBody();
		if (!Optional.ofNullable(user).isPresent()) {
			throw new GitAssesmentException(HttpStatus.BAD_REQUEST, new ExceptionPayload("Invalid user Id provided"));
		}
		final String repositoriesUrl = user.getRepos_url();

		final ResponseEntity<List<RepositoryDetails>> rateResponse = restTemplate.exchange(repositoriesUrl,
				HttpMethod.GET, request, new ParameterizedTypeReference<List<RepositoryDetails>>() {
				});
		final List<RepositoryDetails> list = rateResponse.getBody();
		return list.parallelStream().limit(3).map(RepositoryDetails::getName).collect(Collectors.toList());

	}

}
