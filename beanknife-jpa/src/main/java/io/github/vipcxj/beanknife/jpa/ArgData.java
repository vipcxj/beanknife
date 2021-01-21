package io.github.vipcxj.beanknife.jpa;

import java.util.List;

public class ArgData {
    private boolean extra;
    private Object varKey;
    private List<String> path;

    private ArgData(boolean extra, Object varKey, List<String> path) {
        this.extra = extra;
        this.varKey = varKey;
        this.path = path;
    }

    public static ArgData extraVar(Object key) {
        return new ArgData(true, key, null);
    }

    public static ArgData pathVar(List<String> path) {
        return new ArgData(false, null, path);
    }

    public boolean isExtra() {
        return extra;
    }

    public Object getVarKey() {
        return varKey;
    }

    public List<String> getPath() {
        return path;
    }
}
