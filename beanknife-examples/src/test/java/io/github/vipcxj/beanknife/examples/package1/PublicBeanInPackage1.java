package io.github.vipcxj.beanknife.examples.package1;

import io.github.vipcxj.beanknife.BeanKnife;

@BeanKnife
public class PublicBeanInPackage1 {

    public int publicIntField;
    protected int protectedIntField;
    int defaultIntField;
    private int privateIntField;

    public int getPrivateIntField() {
        return privateIntField;
    }
}
