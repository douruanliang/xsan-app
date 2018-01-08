/**
 * 
 */
package org.security.app;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.security.app.jwt.AppJwtTokenEnHancer;
import org.security.core.properties.OAuth2ClientProperties;
import org.security.core.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author dourl
 * 认证服务器(如果没有继承则走默认)
 */
@Configuration
@EnableAuthorizationServer
public class AppAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter{
    @Autowired
	private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    private TokenStore tokenStore;
    @Autowired(required =false)
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Autowired(required =false)
    private TokenEnhancer jwtTokenEnhancer;
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    	// TODO 指定成我们自己的 bean
    	endpoints.tokenStore(tokenStore)
    	.authenticationManager(authenticationManager)
    	.userDetailsService(userDetailsService);
    	
    	if(null!= jwtAccessTokenConverter && null!= jwtTokenEnhancer){
    		TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
    		List<TokenEnhancer> enhancers = new ArrayList<>();
    		enhancers.add(jwtTokenEnhancer);
    		enhancers.add(jwtAccessTokenConverter);
    		enhancerChain.setTokenEnhancers(enhancers);
    		endpoints.tokenEnhancer(enhancerChain)
    		 .accessTokenConverter(jwtAccessTokenConverter);
    	}
    }
    
     @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    	// TODO Auto-generated method stub
    	//super.configure(clients);
    	 InMemoryClientDetailsServiceBuilder builder =clients.inMemory();
    	 
    	 if(ArrayUtils.isNotEmpty(securityProperties.getOauth2().getClients())){
    		 for(OAuth2ClientProperties client : securityProperties.getOauth2().getClients()){
    			 builder.withClient(client.getClientId())
    	    	 .secret(client.getCllietSecret())
    	    	 .authorizedGrantTypes("refresh_token","authorization_code","password")
    	    	 .accessTokenValiditySeconds(client.getAccessTokenValidateSeconds())
    	    	 .refreshTokenValiditySeconds(2592000)
    	    	 .scopes("all","read","write");
    		 }
    	 }
    	 
    }
	
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
     
}
