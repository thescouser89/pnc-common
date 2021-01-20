package org.jboss.pnc.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringsTest {

    @Test
    public void stripEndingSlash() {
        String string = "http://host.com/path";
        Assertions.assertEquals(string, Strings.stripEndingSlash(string + "/"));
        Assertions.assertEquals(string, Strings.stripEndingSlash(string));
    }

    @Test
    public void stripTrailingSlash() {
        String string = "path/to";
        Assertions.assertEquals(string, Strings.stripTrailingSlash("/" + string));
        Assertions.assertEquals(string, Strings.stripTrailingSlash(string));
    }

    @Test
    public void addEndingSlash() {
        String string = "http://host.com/path";
        Assertions.assertEquals(string + "/", Strings.addEndingSlash(string));
        Assertions.assertEquals(string + "/", Strings.addEndingSlash(string + "/"));
        Assertions.assertNotEquals(string + "//", Strings.addEndingSlash(string + "/"));
    }

}
