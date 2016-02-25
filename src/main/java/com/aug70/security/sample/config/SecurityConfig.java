package com.aug70.security.sample.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
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
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@Configuration
public class SecurityConfig {

	@Configuration
	@EnableWebMvcSecurity
	// After the resource server, so it will never do anything currently
	@Order(4)
	protected static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		protected void globalAuthentication(AuthenticationManagerBuilder auth)
				throws Exception {
			auth.inMemoryAuthentication().withUser("tester").password("121212")
					.roles("USER");
		}

		@Bean(name = "authenticationManager")
		@Override
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			// @formatter:off
			http
            	.anonymous().disable()
            	.requiresChannel().anyRequest().requiresSecure();

			http
				.authorizeRequests().anyRequest().authenticated()
			.and()
				.httpBasic();
			// @formatter:on

		}

	}

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfig extends ResourceServerConfigurerAdapter {

		@Autowired
		private ResourceServerTokenServices tokenServices;

		@Override
		public void configure(ResourceServerSecurityConfigurer resources)
				throws Exception {
			resources.tokenServices(tokenServices);
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {

			// @formatter:off
			http
            	.requiresChannel().anyRequest().requiresSecure();

			// API calls
			http
				.authorizeRequests()
	        	.antMatchers("/**") // Need to narrow this or the main WebSecurityConfigurerAdapter
	            .access("#oauth2.hasScope('trust') and (hasRole('ROLE_USER'))");
				
			// @formatter:on
		}

	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfig extends
			AuthorizationServerConfigurerAdapter {

		@Autowired
		private RedisConnectionFactory redisConnectionFactory;
		
		@Autowired
		private ClientDetailsService clientDetailsService;

		@Autowired
		private AuthenticationManager authenticationManager;

		@Bean
		public TokenStore tokenStore() {
			return new RedisTokenStore(redisConnectionFactory);
		}

		@Bean
		public ApprovalStore approvalStore() {
			TokenApprovalStore store = new TokenApprovalStore();
			store.setTokenStore(tokenStore());
			return store;
		}

		@Primary
		@Bean
		public DefaultTokenServices tokenServices() throws Exception {
			DefaultTokenServices tokenServices = new DefaultTokenServices();
			tokenServices.setAccessTokenValiditySeconds(6000);
			tokenServices.setClientDetailsService(clientDetailsService);
			// tokenServices.setTokenEnhancer(new MyTokenEnhancer());
			tokenServices.setSupportRefreshToken(true);
			tokenServices.setTokenStore(tokenStore());
			return tokenServices;
		}

		@Bean
		public UserApprovalHandler userApprovalHandler() throws Exception {
			MyUserApprovalHandler handler = new MyUserApprovalHandler();
			handler.setApprovalStore(approvalStore());
			handler.setClientDetailsService(clientDetailsService);
			handler.setRequestFactory(new DefaultOAuth2RequestFactory(
					clientDetailsService));
			handler.setUseApprovalStore(true);
			return handler;
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints)
				throws Exception {
			endpoints.tokenServices(tokenServices())
					.userApprovalHandler(userApprovalHandler())
					.authenticationManager(authenticationManager);
		}

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			clients.inMemory()
					.withClient("sample-client")
					.secret("11111111-1111-1111-1111-111111111111")
					.authorizedGrantTypes("password", "authorization_code",
							"refresh_token", "client_credentials")
					.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT").scopes("trust");
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer)
				throws Exception {
			oauthServer.realm("sample/clients").allowFormAuthenticationForClients();
		}

	}

}