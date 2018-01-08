package org.security.app;

import org.security.app.jwt.AppJwtTokenEnHancer;
import org.security.core.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

@Configuration
public class AppTokenStoreConfig {
    @Autowired
	private RedisConnectionFactory redisConnectionFactory;
	@Bean
	@ConditionalOnProperty(prefix="xsan.security.oauth2",name="storeType",havingValue ="redis")
	public TokenStore redisTokenStore(){
		
		return new RedisTokenStore(redisConnectionFactory);
		
	}
	
	@Configuration
	@ConditionalOnProperty(prefix="xsan.security.oauth2",name="storeType",havingValue ="jwt",matchIfMissing=true)
	public static class JwtTokenConfig{
		
		@Autowired
	    private SecurityProperties securityProperties;
		@Bean
		public TokenStore jwtTokenStroe(){
			return new JwtTokenStore(jwtAccessTokenConverter());
		}
		
		@Bean
		public JwtAccessTokenConverter jwtAccessTokenConverter(){
			/*负责生成*/
			JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
			accessTokenConverter.setSigningKey(securityProperties.getOauth2().getJwtSigningKey());
			return accessTokenConverter;
		
		}
		
		@Bean
		@ConditionalOnMissingBean(name="jwtTokenEnhancer")
		public TokenEnhancer jwtTokenEnhancer(){
			/**
			 * 自定义增强处理 通过配置文件可以覆盖默认的
			 */
			return new AppJwtTokenEnHancer();
			
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
