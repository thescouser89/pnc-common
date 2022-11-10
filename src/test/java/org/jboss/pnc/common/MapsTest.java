package org.jboss.pnc.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class MapsTest {

    @Test
    public void shouldRemaplceMapValue() {
        Map<String, Object> root = new HashMap<>();
        Map<String, Object> mapA = new HashMap<>();
        Map<String, Object> mapB = new HashMap<>();
        root.put("a", mapA);
        root.put("b", mapB);
        root.put("c", new HashMap<>());

        Map<String, String> mapBB = new HashMap<>();
        mapB.put("bb", mapBB);
        mapB.put("bc", new HashMap<>());

        mapBB.put("bbb", "my value");
        Maps.deepReplace(root, "/b/bb/bbb", "new value");
        Assertions.assertEquals("new value", ((Map) ((Map) root.get("b")).get("bb")).get("bbb"));
    }
}
