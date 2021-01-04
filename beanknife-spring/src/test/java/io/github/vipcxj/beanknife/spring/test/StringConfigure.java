package io.github.vipcxj.beanknife.spring.test;

import io.github.vipcxj.beanknife.spring.test.beans.SpringBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StringConfigure {

    @Bean
    public SpringBean getSpringBean() {
        return new SpringBean();
    }
}
