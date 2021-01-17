package io.github.vipcxj.beanknife.core.utils;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.*;

public class VarMapper {

    private final Set<String> initVars;
    private final IdentityHashMap<Object, String> mapper;

    public VarMapper(String... initVars) {
        this(Arrays.asList(initVars));
    }

    public VarMapper(@CheckForNull Collection<String> initVars) {
        this.initVars = initVars != null ? new HashSet<>(initVars) : Collections.emptySet();
        this.mapper = new IdentityHashMap<>();
    }

    @NonNull
    public String getVar(@NonNull Object key, @NonNull String name) {
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
