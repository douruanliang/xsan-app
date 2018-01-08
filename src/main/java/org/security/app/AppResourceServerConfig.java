package org.security.app;

import org.security.core.authentiction.mobile.SmsCodeAuthenticationSecurityConfig;
import org.security.core.properties.SecurityConstants;
import org.security.core.properties.SecurityProperties;
import org.security.core.validata.code.ValidateCodeSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * 资源服务器
 * @author dourl
 *
 */
@Configuration
@EnableResourceServer
public class AppResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Autowired   
	protected AuthenticationSuccessHandler defaultAuthenticationSuccessHandler;
	@Autowired
	protected AuthenticationFailureHandler defaultAuthenticationFailureHandler;
	
	@Autowired
	private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;
	
	@Autowired
	private ValidateCodeSecurityConfig validateCodeSecurityConfig;
	
	@Autowired
	private SpringSocialConfigurer xsanSocialSecurityConfig;
	
	@Autowired
	private SecurityProperties securityProperties;
	
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.formLogin()
			.loginPage(SecurityConstants.DEFAULT_UNAUTHENTICATION_URL)
			.loginProcessingUrl(SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_FORM)
			.successHandler(defaultAuthenticationSuccessHandler)
			.failureHandler(defaultAuthenticationFailureHandler);
		http.apply(validateCodeSecurityConfig)
		.and()
	     .apply(smsCodeAuthenticationSecurityConfig)
		 .and()
	     .apply(xsanSocialSecurityConfig)
		  .and()
		  .authorizeRequests()
			.antMatchers(
				SecurityConstants.DEFAULT_UNAUTHENTICATION_URL,
				SecurityConstants.DEFAULT_LOGIN_PROCESSING_URL_MOBILE,
				securityProperties.getBrowser().getLoginPage(),
				SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX+"/*",
				securityProperties.getBrowser().getRegistPage(),
				securityProperties.getBrowser().getSession().getSessionInvalidUrl()+".json",
				securityProperties.getBrowser().getSession().getSessionInvalidUrl()+".html",
				securityProperties.getBrowser().getLogOut(),
				"/user/regist","/social/signUp")
				.permitAll()
			.anyRequest()
			.authenticated()
			.and()
			.csrf().disable();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
