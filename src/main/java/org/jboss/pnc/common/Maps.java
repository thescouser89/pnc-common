package org.jboss.pnc.common;

import java.util.Map;

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

}
