package org.security.app.social;

import org.apache.commons.lang.StringUtils;
import org.security.core.social.DefaultSpringSocialConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class SpringSocialConfigurePostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		//当是 浏览器的 配置时
		if(StringUtils.equals(beanName, "defaultSocialSecurityConfig")){
			DefaultSpringSocialConfigurer  configurer  = (DefaultSpringSocialConfigurer)bean;
			configurer.signupUrl("/social/signUp");
			
			return configurer;
		}
		return bean;
	}

}
