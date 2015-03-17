package com.aug70.security.sample.config;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        WebApplicationContext context = getContext();
        servletContext.addListener(new ContextLoaderListener(context));
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("appServlet", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/*");
        
        // Encoding filter
        FilterRegistration encodingFilter = servletContext.addFilter("encodingFilter", new org.springframework.web.filter.CharacterEncodingFilter());
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
        encodingFilter.addMappingForUrlPatterns(null, true, "/*");
        
        // Security filter
        FilterRegistration securityFilter = servletContext.addFilter("securityFilter", new org.springframework.web.filter.DelegatingFilterProxy());
        securityFilter.setInitParameter("targetBeanName", "springSecurityFilterChain");
        securityFilter.addMappingForUrlPatterns(null, true, "/*");

        // Hidden http method filter
        FilterRegistration hiddenHttpMethodFilter = servletContext.addFilter("hiddenHttpMethodFilter", new org.springframework.web.filter.HiddenHttpMethodFilter());
        hiddenHttpMethodFilter.addMappingForUrlPatterns(null, true, "/*");
        
        
    }

    private AnnotationConfigWebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation("com.aug70.redrum.config");
        return context;
    }

}