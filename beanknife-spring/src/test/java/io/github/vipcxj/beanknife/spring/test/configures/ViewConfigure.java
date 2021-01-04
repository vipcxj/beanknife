package io.github.vipcxj.beanknife.spring.test.configures;

import io.github.vipcxj.beanknife.runtime.annotations.Dynamic;
import io.github.vipcxj.beanknife.runtime.annotations.InjectProperty;
import io.github.vipcxj.beanknife.runtime.annotations.NewViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;
import io.github.vipcxj.beanknife.spring.test.beans.SimpleBean;
import io.github.vipcxj.beanknife.spring.test.beans.SimpleBeanMeta;
import io.github.vipcxj.beanknife.spring.test.beans.SpringBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ViewOf(value = SimpleBean.class, includePattern = ".*")
@Component
public class ViewConfigure {

    private SpringBean springBean;

    @Autowired
    public void setSpringBean(SpringBean springBean) {
        this.springBean = springBean;
    }

    @NewViewProperty("ab")
    @Dynamic
    public String getAb(@InjectProperty(SimpleBeanMeta.a) int a, @InjectProperty(SimpleBeanMeta.b) String b) {
        return a + b;
    }

    @NewViewProperty("springBean")
    public SpringBean getSpringBean() {
        return springBean;
    }
}
