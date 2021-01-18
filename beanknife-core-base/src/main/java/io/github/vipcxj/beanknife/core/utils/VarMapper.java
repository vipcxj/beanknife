package io.github.vipcxj.beanknife.core.utils;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.*;

public class VarMapper {

    private final Set<String> initVars;
    private final IdentityHashMap<Object, String> mapper;
    private boolean used;

    public VarMapper(String... initVars) {
        this(Arrays.asList(initVars));
    }

    public VarMapper(@CheckForNull Collection<String> initVars) {
        this.initVars = initVars != null ? new HashSet<>(initVars) : Collections.emptySet();
        this.mapper = new IdentityHashMap<>();
        this.used = false;
    }

    public void addInitVar(String var) {
        if (used) {
            throw new IllegalStateException("addInitVar method should be called before any getVar methods. use appendVar instead.");
        }
        this.initVars.add(var);
    }

    public String appendVar(String var) {
        if (initVars.contains(var) || mapper.containsValue(var)) {
            return appendVar(var + "_");
        }
        return var;
    }

    @NonNull
    public String getVar(@NonNull Object key, @NonNull String name) {
        if (!this.used) {
            this.used = true;
        }
        String result = mapper.get(key);
        if (result != null) {
            return result;
        }
        if (!initVars.contains(name) && !mapper.containsValue(name)) {
            mapper.put(key, name);
            return name;
        } else {
            return getVar(key, name + "_");
        }
    }
}
