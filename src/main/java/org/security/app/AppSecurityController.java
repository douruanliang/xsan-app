package org.security.app;

import javax.servlet.http.HttpServletRequest;

import org.security.app.social.AppSingUpUtils;
import org.security.core.support.SocialUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

@RestController
public class AppSecurityController {
	@Autowired
    private ProviderSignInUtils providerSignInUtils;
	@Autowired
    private AppSingUpUtils appSingUpUtils;
	
	/**
	 * 引导用户去注册、认证
	 */
	@GetMapping("/social/signUp")
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public SocialUserInfo getSocialUserInfo(HttpServletRequest request) {
		SocialUserInfo userInfo = new SocialUserInfo();
		Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));
		userInfo.setProviderId(connection.getKey().getProviderId());
		userInfo.setProviderUserId(connection.getKey().getProviderUserId());
		userInfo.setNickname(connection.getDisplayName());
		userInfo.setHeadimg(connection.getImageUrl());
		
		//每次保存到redis里面
		appSingUpUtils.saveConnectionData(new ServletWebRequest(request), connection.createData());
		return userInfo;
	}
	
	
	
	
	
	
	
	
	
	
	
}