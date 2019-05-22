package com.assesment.centurylink.dto;

import java.util.List;

import lombok.Data;

@Data
public class StargazersDto {

	private String repositoryName;
	private List<String> stargazersList;
}
