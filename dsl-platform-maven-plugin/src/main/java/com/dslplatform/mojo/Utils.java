package com.dslplatform.mojo;

import com.dslplatform.compiler.client.CompileParameter;
import com.dslplatform.compiler.client.parameters.Settings;
import com.dslplatform.compiler.client.parameters.Targets;
import com.dslplatform.compiler.client.parameters.*;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.net.URL;

public class Utils {

    public static final CompileParameter[] ALL_COMPILE_PARAMETERS = {
        //Help.INSTANCE, // TODO: See what to do with this one
        //PropertiesFile.INSTANCE, // TODO: See what to do with this one
        GrantRole.INSTANCE,
        DslCompiler.INSTANCE,
        Version.INSTANCE,
        Diff.INSTANCE,
        Settings.INSTANCE,
        PostgresConnection.INSTANCE,
        TempPath.INSTANCE,
        Targets.INSTANCE,
        Migration.INSTANCE,
        VarraySize.INSTANCE,
        JavaPath.INSTANCE,
        DisableColors.INSTANCE,
        ApplyMigration.INSTANCE,
        DslPath.INSTANCE,
        LogOutput.INSTANCE,
        Namespace.INSTANCE,
        Mono.INSTANCE,
        OracleConnection.INSTANCE,
        Prompt.INSTANCE,
        Dependencies.INSTANCE,
        Parse.INSTANCE,
        Download.INSTANCE,
        Force.INSTANCE,
        Maven.INSTANCE,
        SqlPath.INSTANCE,
        DotNet.INSTANCE,
        ScalaPath.INSTANCE
    };

    private final Log log;
    public Utils(Log log) {
        this.log = log;
    }

    public String resourceAbsolutePath(String resource) {
        if(resource == null) return null;

        try {
            String prefix = resource.startsWith("/") ? "" : "/";
            URL resourceUrl = Utils.class.getResource(prefix + resource);
            if (resourceUrl != null) {
                return new File(resourceUrl.toURI()).getAbsolutePath();
            }
        } catch(Exception e) {
        }

        File result = new File(resource);
        if(result.exists()) return result.getAbsolutePath();

        return null;
    }

    public String createDirIfNotExists(String dir) {
        String absolutePath = resourceAbsolutePath(dir);
        if(absolutePath == null) {
            File file = new File(dir);
            if(!file.exists())
                file.mkdirs();
            return resourceAbsolutePath(dir);
        }
        return absolutePath;
    }

    public static Targets.Option targetOptionFrom(String value) {
        for(Targets.Option option : Targets.Option.values()) {
            if(nulleq(option.toString(), value)) return option;
        }
        return null;
    }

    public static Settings.Option settingsOptionFrom(String value) {
        for(Settings.Option option : Settings.Option.values()) {
            if(nulleq(option.toString(), value)) return option;
        }
        return null;
    }

    public static CompileParameter compileParameterFrom(String value) {
        for(CompileParameter compileParameter : ALL_COMPILE_PARAMETERS) {
            if(nulleq(compileParameter.getAlias(), value)) return compileParameter;
        }
        return null;
    }

    private static <T> boolean nulleq(T left, T right) {
        if(left == null && right == null) return true;
        if(left != null) return left.equals(right);
        if(right != null) return right.equals(left);
        return false;
    }
}
