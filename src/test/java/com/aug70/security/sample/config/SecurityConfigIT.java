package com.aug70.security.sample.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


public class SecurityConfigIT extends AbstractControllerBaseIT {
	
	@Test
	public void getOAuth2TokenPassword() throws Exception {

		mockMvc.perform(
				post("/oauth/token")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.content("{\"grant_type\" : \"password\", \"client_id\":\"sample-client\", \"client_secret\":\"11111111-1111-1111-1111-111111111111\", \"scope\", \"play trust\", \"username\"=\"tester\", \"password\"=\"121212\"}".getBytes()))
				.andExpect(status().is(HttpStatus.OK.value()));
	}

	@Test
	public void getOAuth2TokenClientCredentials() throws Exception {

		mockMvc.perform(
				post("/oauth/token")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.accept(MediaType.APPLICATION_JSON)
				.content("{\"grant_type\" : \"client_credentials\", \"client_id\":\"sample-client\", \"client_secret\":\"11111111-1111-1111-1111-111111111111\", \"scope\", \"play trust\"}".getBytes()))
				.andExpect(status().is(HttpStatus.OK.value()));
	}

}