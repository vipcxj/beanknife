package io.github.vipcxj.beanknife.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CollectionUtils {

    public static <K1, K2, V> Map<K2, V> mapKey(Map<K1, V> map, Function<K1, K2> mapper) {
        Map<K2, V> out = new HashMap<>();
        for (Map.Entry<K1, V> entry : map.entrySet()) {
            out.put(mapper.apply(entry.getKey()), entry.getValue());
        }
        return out;
    }
}
