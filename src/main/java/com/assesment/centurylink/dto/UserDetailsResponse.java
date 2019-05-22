package com.assesment.centurylink.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class UserDetailsResponse {

	private String userId;
	private List<UserDetailsResponse> followers;

}
