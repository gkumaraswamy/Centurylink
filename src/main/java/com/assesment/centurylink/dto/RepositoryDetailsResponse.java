package com.assesment.centurylink.dto;

import java.util.List;

import lombok.Data;

@Data
public class RepositoryDetailsResponse {

	private String userId;
	private List<StargazersDto> repositoriesList;
}
