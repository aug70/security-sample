package com.aug70.security.sample.config;

import javax.servlet.Filter;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy (value = { 
	@ContextConfiguration(classes = { WebMvcConfig.class })
})
@Transactional
public abstract class AbstractControllerBaseIT {

	protected static final Logger logger = LoggerFactory.getLogger(AbstractControllerBaseIT.class);

	@Autowired
	protected Environment env;

	@Autowired
	protected WebApplicationContext webApplicationContext;

	@Autowired
	@Qualifier("springSecurityFilterChain")
	protected Filter filter;
	
	protected MockMvc mockMvc;
	
	@Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).addFilters(filter).build();
	}
	
	@After
	public void tearDown() {

	}


}