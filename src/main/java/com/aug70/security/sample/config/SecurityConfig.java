package com.aug70.security.sample.config;

import javax.annotation.Priority;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configuration.ClientDetailsServiceConfiguration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

	@Autowired
	private ClientDetailsService clientDetailsService;
	
	@Bean
	public TokenStore tokenStore() {
		return new InMemoryTokenStore();
	}
	
	@Bean
	public ApprovalStore approvalStore() {
		TokenApprovalStore store = new TokenApprovalStore();
		store.setTokenStore(tokenStore());
		return store;
	}
	
	@Bean
	public OAuth2AccessDeniedHandler oAuth2AccessDeniedHandler() {
		return new OAuth2AccessDeniedHandler();
	}
	
	@Primary
	@Bean
	public DefaultTokenServices tokenServices() throws Exception {
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setAccessTokenValiditySeconds(6000);
		tokenServices.setClientDetailsService(clientDetailsService);
		tokenServices.setTokenEnhancer(new MyTokenEnhancer());
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setTokenStore(tokenStore());
		return tokenServices;
	}
	
	@Bean
	public UserApprovalHandler userApprovalHandler() throws Exception {
		MyUserApprovalHandler handler = new MyUserApprovalHandler();
		handler.setApprovalStore(approvalStore());
		handler.setClientDetailsService(clientDetailsService);
		handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		handler.setUseApprovalStore(true);
		return handler;
	}

	@Priority(2000)
	@Configuration
	@EnableWebSecurity
	protected static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Resource
		private PasswordEncoder passwordEncoder;

		@Autowired
		private OAuth2AccessDeniedHandler oAuth2AccessDeniedHandler;
		
		@Value("${baseUrl}") 
		private String baseUrl;
		
		@Bean
		UserDetailsService clientDetailsUserDetailsService() throws Exception {
		    return new ClientDetailsUserDetailsService(clientDetailsService());
		}
		
		@Bean
		public ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter() throws Exception {
			ClientCredentialsTokenEndpointFilter filter = new ClientCredentialsTokenEndpointFilter();
			filter.setAuthenticationManager(authenticationManagerBean());
			filter.afterPropertiesSet();
			return filter;
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			
			auth.inMemoryAuthentication()
            .withUser("tester").password("121212").roles("ROLE_USER");
			
			auth.userDetailsService(clientDetailsUserDetailsService());

		}
		
		@Bean(name="authenticationManager")
		@Override
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}
		
		@Bean
		protected AuthenticationEntryPoint authenticationEntryPoint() {
			OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
			entryPoint.setTypeName("Basic");
			entryPoint.setRealmName("sample/client");
			return entryPoint;
		}
		
		@Override
		public void configure(WebSecurity webSecurity) throws Exception {
			webSecurity
				.debug(true);
		}
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			
			// @formatter:off
			http
            	.anonymous().disable()
            	.requiresChannel().anyRequest().requiresSecure();

			http
				.antMatcher("/oauth/token")
				.authorizeRequests().anyRequest().authenticated()
			.and()
				.httpBasic().authenticationEntryPoint(authenticationEntryPoint())
			.and()
				.csrf().requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/token")).disable()
				.exceptionHandling().accessDeniedHandler(oAuth2AccessDeniedHandler)
			.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
			http
				.addFilterBefore(clientCredentialsTokenEndpointFilter(), BasicAuthenticationFilter.class);
			// @formatter:on

		}
		
		@Bean
		public ClientDetailsService clientDetailsService() throws Exception {
			ClientDetailsServiceConfiguration serviceConfig = new ClientDetailsServiceConfiguration();
				
			serviceConfig.clientDetailsServiceConfigurer().inMemory()
				.withClient("sample-client")
				.secret("11111111-1111-1111-1111-111111111111")
				.authorizedGrantTypes("password", "authorization_code", "refresh_token", "client_credentials")
				.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
				.scopes("trust");
			
			return serviceConfig.clientDetailsService();
		}
		

		
	}

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfig extends ResourceServerConfigurerAdapter {
		
		@Autowired
		private ResourceServerTokenServices tokenServices;
		
		@Autowired
		private OAuth2AccessDeniedHandler oAuth2AccessDeniedHandler;
		
		@Autowired
		ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter;
		
		@Override
		public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
			resources.tokenServices(tokenServices);
		}
		
		@Override
		public void configure(HttpSecurity http) throws Exception {

			// @formatter:off
			http
            	.requiresChannel().anyRequest().requiresSecure();

			// API calls
			http
				.anonymous().disable()
				.authorizeRequests()
	        	.antMatchers("/**")
	            .access("#oauth2.hasScope('trust') and (hasRole('ROLE_USER'))")
	        .and()
	        	.addFilterBefore(clientCredentialsTokenEndpointFilter, BasicAuthenticationFilter.class)
	            .sessionManagement()
	            .sessionCreationPolicy(SessionCreationPolicy.NEVER)
	        .and()
	            .exceptionHandling()
	            .accessDeniedHandler(oAuth2AccessDeniedHandler);
				
			// @formatter:on
		}
		
	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
		
		@Autowired
		private AuthorizationServerTokenServices tokenServices;
		
		@Autowired
		private ClientDetailsService clientDetailsService;
	
		@Autowired
		private UserApprovalHandler userApprovalHandler;
		
		@Autowired
		private AuthenticationManager authenticationManager;
		
		@Autowired
		AuthenticationEntryPoint authenticationEntryPoint;

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints
			.tokenServices(tokenServices)
			.userApprovalHandler(userApprovalHandler)
			.authenticationManager(authenticationManager);
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients)
				throws Exception {
			clients.withClientDetails(clientDetailsService);
		}
		
		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer)
				throws Exception {
			oauthServer.authenticationEntryPoint(authenticationEntryPoint)
					.realm("sample/clients");
		}
		
	}

}