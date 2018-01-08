package org.security.app.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.security.core.properties.LoginType;
import org.security.core.properties.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 自定义登录成功后处理行为
 * 
 * 一般 形式 implements AuthenticationSuccessHandler
 * 
 * 默认父类的方法就是跳转
 * 
 * @author dourl
 *
 */
@Component("defaultAuthenticationSuccessHandler")
public class DefaultAuthenticationSuccessHandller extends SavedRequestAwareAuthenticationSuccessHandler {
	private Logger logger = LoggerFactory.getLogger(getClass());

	// spring boot 启动的时候会有一个
	@Autowired
	private ObjectMapper ObjectMapper;

	@Autowired
	private SecurityProperties securityProperties;
	
	@Autowired
	private ClientDetailsService clientDetailsService;
	@Autowired
	private AuthorizationServerTokenServices authorizationServerTokenServices;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		logger.info("登录成功");
		String header = request.getHeader("Authorization");

		if (header == null || !header.startsWith("Basic ")) {
			throw new UnapprovedClientAuthenticationException("请求头中无Client信息");
		}

			String[] tokens = extractAndDecodeHeader(header, request);
			assert tokens.length == 2;

			String clientId = tokens[0];

			String clientSecret =tokens[1];
			
			ClientDetails clientDetails= clientDetailsService.loadClientByClientId(clientId);
		
			if(null == clientDetails){
				throw new UnapprovedClientAuthenticationException("ClientId 对应的配置信息不存在："+clientId);
			}else if(!StringUtils.equals(clientDetails.getClientSecret(), clientSecret)){
				//判断传入的 密码是否正确
				throw new UnapprovedClientAuthenticationException("clientSecret 不匹配："+clientSecret);
			}
			
			TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, 
					clientId, clientDetails.getScope(), "custom");
			
			OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
			OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
			OAuth2AccessToken token =authorizationServerTokenServices.createAccessToken(oAuth2Authentication);

			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(ObjectMapper.writeValueAsString(token));

	}

	
	
	private String[] extractAndDecodeHeader(String header, HttpServletRequest request)
			throws IOException {

		byte[] base64Token = header.substring(6).getBytes("UTF-8");
		byte[] decoded;
		try {
			decoded = Base64.decode(base64Token);
		}
		catch (IllegalArgumentException e) {
			throw new BadCredentialsException(
					"Failed to decode basic authentication token");
		}

		String token = new String(decoded,"UTF-8" );

		int delim = token.indexOf(":");

		if (delim == -1) {
			throw new BadCredentialsException("Invalid basic authentication token");
		}
		return new String[] { token.substring(0, delim), token.substring(delim + 1) };
	}
}
