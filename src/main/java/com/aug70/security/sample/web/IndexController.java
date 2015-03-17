package com.aug70.security.sample.web;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@PropertySource(value = {"classpath:application.properties"})
public class IndexController {

	private String baseUrl;

	@Autowired
	Environment env;

	@PostConstruct
	void initialize() {
		this.baseUrl = env.getProperty("baseUrl");
	}
	
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	@Resource
	private AuthorizationEndpoint authorizationEndpoint;
	
	@RequestMapping({"/"})
	public String index() {
		logger.debug("Redirecting to base url {}", baseUrl + "/index.html");
		return "redirect:" + baseUrl + "/index.html";
	}

}
