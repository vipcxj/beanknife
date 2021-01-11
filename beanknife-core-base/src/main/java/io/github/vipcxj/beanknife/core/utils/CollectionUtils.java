package io.github.vipcxj.beanknife.core.utils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtils {

    public static <K1, K2, V> Map<K2, V> mapKey(Map<K1, V> map, Function<K1, K2> mapper) {
        Map<K2, V> out = new HashMap<>();
        for (Map.Entry<K1, V> entry : map.entrySet()) {
            out.put(mapper.apply(entry.getKey()), entry.getValue());
        }
        return out;
    }

    public static <T> List<List<T>> checkUnique(Collection<T> collection, Comparator<? super T> comparator) {
        TreeMap<T, List<T>> checks = new TreeMap<>(comparator);
        for (T el : collection) {
            List<T> check = checks.get(el);
            if (check == null) {
                List<T> value = new ArrayList<>();
                value.add(el);
                checks.put(el, value);
            } else {
                check.add(el);
            }
        }
        return checks.values().stream().filter(v -> v.size() > 1).collect(Collectors.toList());
    }
}
