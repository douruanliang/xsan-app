package org.security.app.validate.code.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.security.core.validata.code.ValidateCode;
import org.security.core.validata.code.ValidateCodeException;
import org.security.core.validata.code.ValidateCodeRepository;
import org.security.core.validata.code.ValidateCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

@Component
public class RedisValidateCodeRepository implements ValidateCodeRepository {

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;
	
	/**
	 * 默认30分钟
	 */
	@Override
	public void save(ServletWebRequest request, ValidateCode code, ValidateCodeType validateCodeType) {
		redisTemplate.opsForValue().set(buildKey(request,validateCodeType),code,30,TimeUnit.MINUTES);

	}

	

	@Override
	public ValidateCode get(ServletWebRequest request, ValidateCodeType validateCodeType) {
		Object value = redisTemplate.opsForValue().get(buildKey(request, validateCodeType));
		if(value == null){
			return null;
		}
		
		return (ValidateCode) value;
	}

	@Override
	public void removeget(ServletWebRequest request, ValidateCodeType validateCodeType) {
		redisTemplate.delete(buildKey(request, validateCodeType));

	}
	
	private Object buildKey(ServletWebRequest request, ValidateCodeType validateCodeType) {
		String deviceId = request.getHeader("deviceId");
		if(StringUtils.isBlank(deviceId)){
			throw new ValidateCodeException("请在请求头中添加deviceId参数");
		}
		return "code:"+validateCodeType.toString().toLowerCase()+":"+deviceId;
	}

}
