package org.jboss.pnc.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class Strings {

    /**
     * Check if the given string is null or contains only whitespace characters.
     *
     * @param string String to check for non-whitespace characters
     * @return boolean True if the string is null, empty, or contains only whitespace (empty when trimmed). Otherwise
     *         return false.
     */
    public static boolean isEmpty(String string) {
        if (string == null) {
            return true;
        }
        return string.trim().isEmpty();
    }

    /**
     * Converts string with key:value1|value2,key2:value22 to a map where key is an entry key and values are a list of
     * items.
     */
    public static Map<String, List<String>> toMap(String string) {
        if (string == null || string.equals("")) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> map = new HashMap<>();
        try {
            String[] pairs = string.split(",");

            for (String pair : pairs) {
                String[] keyValues = pair.split(":");
                String valuesString = keyValues[1];
                String[] values = valuesString.split("\\|");
                map.put(keyValues[0], Arrays.asList(values));
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Invalid key:value string: [" + string + "]", e);
        }
        return map;
    }

    /**
     * Remove ending slash if present and return the string without ending slash
     *
     * @param string
     * @return
     */
    public static String stripEndingSlash(String string) {
        if (string == null) {
            return null;
        }
        if (string.endsWith("/")) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

    /**
     * Remove slash at the begining if present and return the string without ending slash
     *
     * @param string
     * @return
     */
    public static String stripTrailingSlash(String string) {
        if (string == null) {
            return null;
        }
        if (string.startsWith("/")) {
            string = string.substring(1);
        }
        return string;
    }

    /**
     * Adds ending slash if it is not present.
     *
     * @param string
     * @return
     */
    public static String addEndingSlash(String string) {
        if (string == null) {
            return null;
        }
        if (!string.endsWith("/")) {
            string += "/";
        }
        return string;
    }

    /**
     * If the value is not empty the value is returned otherwise the defaultValue is returned.
     *
     * @param value
     * @param defaultValue
     * @return
     */
    public static String valueOrDefault(String value, String defaultValue) {
        if (!isEmpty(value)) {
            return value;
        } else {
            return defaultValue;
        }
    }

    public static String fistCharToLower(String string) {
        char c[] = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }
}
