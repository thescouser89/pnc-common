package org.jboss.pnc.common;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

public class Maps {

    /**
     * Replaces a value at given path in a multidimensional map.
     *
     * @param map
     * @param path Path to the value that needs to be replaced eg. "/path/to/key"
     * @param newValue Replacement value.
     */
    public static void deepReplace(Map<String, Object> map, String path, Object newValue) {
        String[] keys = Strings.stripTrailingSlash(path).split("/");
        Object currentValue = map;
        StringBuilder currentPath = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            currentPath.append("/").append(keys[i]);
            if (i == keys.length - 1) { // is it the key targeted by path
                ((Map) currentValue).put(keys[i], newValue);
            } else if (currentValue instanceof Map) {
                currentValue = ((Map) currentValue).get(keys[i]);
            } else {
                throw new java.lang.IllegalArgumentException(
                        "Object at path " + currentPath + " must be an instance of Map.");
            }
        }
    }

    /**
     * Check if a map key exists on the path of nested maps.
     *
     * @param map
     * @param path
     * @return true if the key for given path exists
     */
    public static boolean deepContainsKey(Map<String, Object> map, String path) {
        String[] keys = Strings.stripTrailingSlash(path).split("/");
        Object currentValue = map;
        for (int i = 0; i < keys.length; i++) {
            if (currentValue instanceof Map) {
                boolean containsKey = ((Map<?, ?>) currentValue).containsKey(keys[i]);
                if (i == keys.length - 1) { // is it the key targeted by path
                    return containsKey;
                } else {
                    currentValue = ((Map) currentValue).get(keys[i]);
                }
            }
        }
        return false;
    }

    /**
     * Function creates {@code EnumMap} and initializes it with entries ({@code k}, {@code v}), where {@code k} acquires
     * all enum constants from enum {@code K} and {@code v} is the provided default value.
     *
     * @param keyType class of which are all keys in the {@code EnumMap}
     * @param defaultValue default value for every key
     * @return {@code EnumMap} as described above
     * @param <K> type of keys in the resulting {@code EnumMap}
     * @param <V> type of values in the resulting {@code EnumMap}
     */
    public static <K extends Enum<K>, V> EnumMap<K, V> initEnumMapWithDefaultValue(Class<K> keyType, V defaultValue) {
        EnumMap<K, V> enumMap = new EnumMap<>(keyType);
        Stream.of(keyType.getEnumConstants()).forEach(enumConst -> enumMap.put(enumConst, defaultValue));
        return enumMap;
    }
}
