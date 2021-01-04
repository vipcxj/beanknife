package io.github.vipcxj.beanknife.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BeanUtils.class)
public class SpringBeanProviderAutoConfigure {
}
