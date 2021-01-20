package io.github.vipcxj.beanknife.core.utils;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.*;

public class VarMapper {

    private final Set<String> initVars;
    private final IdentityHashMap<Object, String> mapper;
    private final List<Object> keys;
    private final Map<String, Integer> indexMap;
    private boolean used;

    public VarMapper(String... initVars) {
        this(Arrays.asList(initVars));
    }

    public VarMapper(@CheckForNull Collection<String> initVars) {
        this.initVars = initVars != null ? new HashSet<>(initVars) : Collections.emptySet();
        this.mapper = new IdentityHashMap<>();
        this.keys = new ArrayList<>();
        this.indexMap = new HashMap<>();
        this.used = false;
    }

    public VarMapper(VarMapper other) {
        this.initVars = new HashSet<>(other.initVars);
        this.mapper = new IdentityHashMap<>(other.mapper);
        this.keys = new ArrayList<>(other.keys);
        this.indexMap = new HashMap<>(other.indexMap);
        this.used = other.used;
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

    public String nextVar(String name) {
        Integer index = indexMap.get(name);
        if (index == null) {
            indexMap.put(name, 0);
            return name + 0;
        } else {
            return name + index;
        }
    }

    private void consumeVar(String name) {
        indexMap.merge(name, 1, Integer::sum);
    }

    @NonNull
    public String getVar(@NonNull Object key, @NonNull String name) {
        return getVar(key, name, false);
    }

    @NonNull
    public String getVar(@NonNull Object key, @NonNull String name, boolean indexMode) {
        if (!this.used) {
            this.used = true;
        }
        String result = mapper.get(key);
        if (result != null) {
            return result;
        }
        String var = indexMode ? nextVar(name) : name;
        if (indexMode) {
            consumeVar(name);
        }
        if (!initVars.contains(name) && !mapper.containsValue(name)) {
            mapper.put(key, var);
            keys.add(key);
            return var;
        } else {
            return getVar(key, indexMode ? nextVar(name) : (name + "_"), indexMode);
        }
    }

    public List<Object> getKeys() {
        return keys;
    }

    @CheckForNull
    public String getVar(@NonNull Object key) {
        return mapper.get(key);
    }
}
