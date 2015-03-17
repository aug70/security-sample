package com.aug70.security.sample.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"com.aug70.security"})
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		registry.addResourceHandler("/index.html").addResourceLocations("/WEB-INF/resources/index.html");
		registry.addResourceHandler("/swagger/**").addResourceLocations("/WEB-INF/resources/swagger/");
		registry.addResourceHandler("/copyright.html").addResourceLocations("/WEB-INF/resources/copyright.html");
		registry.addResourceHandler("/o2c.html").addResourceLocations("/WEB-INF/resources/o2c.html");
		registry.addResourceHandler("/api-docs/**").addResourceLocations("/WEB-INF/resources/api-docs/");
		
	}

	
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
	    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
	    viewResolver.setViewClass(InternalResourceView.class);
		registry.viewResolver(viewResolver);
	}
	
}