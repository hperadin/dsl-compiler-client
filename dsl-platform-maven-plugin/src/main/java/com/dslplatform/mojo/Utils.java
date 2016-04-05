package com.dslplatform.mojo;

import java.io.File;
import java.net.URL;

public class Utils {

    public static String resourceAbsolutePath(String resource) {
        try {
            String prefix = resource.startsWith("/") ? "" : "/";
            URL resourceUrl = Utils.class.getResource(prefix + resource);
            if (resourceUrl != null) {
                return new File(resourceUrl.toURI()).getAbsolutePath();
            }
        } catch(Exception e) {
        }
        return null;
    }

}
