package io.github.vipcxj.beanknife.spring.test;

import io.github.vipcxj.beanknife.spring.test.beans.SimpleBean;
import io.github.vipcxj.beanknife.spring.test.beans.SimpleBeanView;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TestApplication.class)
public class Tester {

    @Test
    public void test() {
        SimpleBean simpleBean = new SimpleBean();
        simpleBean.setA(1);
        simpleBean.setB("a");
        simpleBean.setC(true);
        Date time1 = new Date();
        SimpleBeanView view = SimpleBeanView.read(simpleBean);
        Date now = view.getSpringBean().now();
        Date time2 = new Date();
        assertEquals(simpleBean.getA(), view.getA());
        assertEquals(simpleBean.getB(), view.getB());
        assertEquals(simpleBean.isC(), view.isC());
        assertEquals(simpleBean.getA() + simpleBean.getB(), view.getAb());
        assertTrue(time1.getTime() <= now.getTime());
        assertTrue(now.getTime() <= time2.getTime());
    }
}
