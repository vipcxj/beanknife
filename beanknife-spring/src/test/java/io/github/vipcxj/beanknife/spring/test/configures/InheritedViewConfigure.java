package io.github.vipcxj.beanknife.spring.test.configures;

import io.github.vipcxj.beanknife.runtime.annotations.NewViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;
import io.github.vipcxj.beanknife.spring.test.beans.SimpleBean;
import io.github.vipcxj.beanknife.spring.test.beans.SpringBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ViewOf(value = SimpleBean.class, genName = "InheritedSimpleBeanView", includePattern = ".*")
@Component
public class InheritedViewConfigure extends ViewConfigure {

    @Autowired
    private SpringBean springBean;

    @NewViewProperty("anotherSpringBean")
    public SpringBean getAnotherSpringBean() {
        return springBean;
    }
}
