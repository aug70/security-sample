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
				post("/oauth/token").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.APPLICATION_JSON)
						.param("grant_type", "password")
						.param("client_id", "sample-client")
						.param("client_secret", "11111111-1111-1111-1111-111111111111")
						.param("scope", "trust").param("username", "tester")
						.param("password", "121212")).andExpect(
				status().is(HttpStatus.OK.value()));
	}

	@Test
	public void getOAuth2TokenClientCredentials() throws Exception {

		mockMvc.perform(
				post("/oauth/token").contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.accept(MediaType.APPLICATION_JSON)
						.param("grant_type", "client_credentials")
						.param("client_id", "sample-client")
						.param("client_secret", "11111111-1111-1111-1111-111111111111")
						.param("scope", "trust")).andExpect(
				status().is(HttpStatus.OK.value()));
	}
}