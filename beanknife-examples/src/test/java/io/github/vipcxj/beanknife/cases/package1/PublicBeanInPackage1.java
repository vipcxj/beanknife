package io.github.vipcxj.beanknife.cases.package1;

import io.github.vipcxj.beanknife.runtime.annotations.ViewMeta;

@ViewMeta
public class PublicBeanInPackage1 {

    public int publicIntField;
    protected int protectedIntField;
    int defaultIntField;
    private int privateIntField;

    public int getPrivateIntField() {
        return privateIntField;
    }
}
