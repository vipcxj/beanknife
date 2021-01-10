package io.github.vipcxj.beanknife.core.utils;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VarMapper {

    private final Map<String, String> mapper;

    public VarMapper(String... initVars) {
        this(Arrays.asList(initVars));
    }

    public VarMapper(@CheckForNull Collection<String> initVars) {
        mapper = new HashMap<>();
        if (initVars != null) {
            for (String initVar : initVars) {
                if (initVar != null) {
                    mapper.put(initVar, initVar);
                }
            }
        }
    }

    @NonNull
    private String calcVar(@NonNull String name, @NonNull String var) {
        String result = mapper.get(name);
        if (result != null) {
            return result;
        }
        if (!mapper.containsValue(var)) {
            mapper.put(name, var);
            return var;
        } else {
            return calcVar(name, var + "_");
        }
    }

    @NonNull
    public String getVar(@NonNull String name) {
        return calcVar(name, name);
    }
}
