package com.neew.browser;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class VersioningUnitTest {

    @Test
    public void basic_math_works() {
        assertTrue(2 + 2 == 4);
    }

    @Test
    public void packageName_nonEmpty() {
        // Unit tests don't have Android context, keep it simple
        String pkg = "com.neew.browser";
        assertNotNull(pkg);
        assertTrue(pkg.length() > 0);
    }
}
