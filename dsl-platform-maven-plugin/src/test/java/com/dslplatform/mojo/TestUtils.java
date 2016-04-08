package com.dslplatform.mojo;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class TestUtils {
    static void assertDir(String path) {
        File dir = new File(path);
        assertTrue("Does not exist: " + path, dir.exists());
        assertTrue("Is not a directory.", dir.isDirectory());
    }

    static void assertFile(String path) {
        File file = new File(path);
        assertTrue("Does not exist: " + path, file.exists());
        assertTrue("Is not a directory.", file.isFile());
    }
}
