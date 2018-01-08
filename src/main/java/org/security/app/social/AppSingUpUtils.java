/**
 * 
 */
package org.security.app.social;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.security.app.exception.AppSecretException;
import org.security.core.validata.code.ValidateCodeException;
import org.security.core.validata.code.ValidateCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * @author dourl
 * 将用户三方信息保存到redis
 */
@Component
public class AppSingUpUtils {
	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;
	@Autowired
	private UsersConnectionRepository usersConnectionRepository;
	@Autowired
	private ConnectionFactoryLocator connectionFactoryLocator;
	
	public void saveConnectionData(WebRequest request,ConnectionData connectionData){
		redisTemplate.opsForValue().set(buildKey(request),connectionData,10,TimeUnit.MINUTES);
	}
	
	/**
	 * 绑定用户信息
	 * @param reques
	 * @param userId
	 */
	public void doPostSignUp(WebRequest request,String userId){
		String key = buildKey(request);
		if(!redisTemplate.hasKey(key)){
			throw new AppSecretException("无法找到缓存的第三方用户社交账号信息");
		}
		ConnectionData connectionData =(ConnectionData) redisTemplate.opsForValue().get(key);
		//拿到连接工厂 比如 QQ 或 微信
		Connection<?> connection =connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId())
		                          .createConnection(connectionData);
		usersConnectionRepository.createConnectionRepository(userId).addConnection(connection);
		
		//清除
		redisTemplate.delete(key);
	}
	
	private String buildKey(WebRequest request) {
		String deviceId = request.getHeader("deviceId");
		if(StringUtils.isBlank(deviceId)){
			throw new ValidateCodeException("设备ID 不能为空");
		}
		return "xsan:security:social.connect."+deviceId;
	}
	
	
	
	
	
	
	
	
	
}
