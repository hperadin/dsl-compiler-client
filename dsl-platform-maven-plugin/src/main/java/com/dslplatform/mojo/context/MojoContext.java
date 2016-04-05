package com.dslplatform.mojo.context;

import com.dslplatform.compiler.client.Context;

public class MojoContext extends Context {
    public final StringBuilder showLog = new StringBuilder();
    public final StringBuilder errorLog = new StringBuilder();
    public final StringBuilder traceLog = new StringBuilder();

    public void show(String... values) {
        for (String v : values) {
            showLog.append(v+"\n");
        }
    }

    public void log(String value) {
        traceLog.append(value+"\n");
    }

    public void log(char[] value, int len) {
        traceLog.append(value, 0, len);
    }

    public void error(String value) {
        errorLog.append(value+"\n");
    }

    public void error(Exception ex) {
        errorLog.append(ex.getMessage());
        traceLog.append(ex.toString());
    }

}
