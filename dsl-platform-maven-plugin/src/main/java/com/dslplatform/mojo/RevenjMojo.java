package com.dslplatform.mojo;

import com.dslplatform.compiler.client.CompileParameter;
import com.dslplatform.compiler.client.Main;
import com.dslplatform.compiler.client.parameters.*;
import com.dslplatform.mojo.context.MojoContext;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.net.URL;
import java.util.List;

@Mojo(name="revenj_java")
public class RevenjMojo
    extends AbstractMojo {


    @Parameter(property = "dslPath")
    private String _dslPath;

    @Parameter(property = "tempPath")
    private String _tempPath;

    public void setDslPath(String dslPath) throws MojoExecutionException {
        this._dslPath = Utils.resourceAbsolutePath(dslPath);
    }

    public void setTempPath(String tempPath) throws MojoExecutionException {
        this._tempPath = Utils.resourceAbsolutePath(tempPath);
    }

    public void execute()
        throws MojoExecutionException
    {
        try {
            MojoContext context = new MojoContext();

            context.put(Targets.Option.REVENJ_JAVA);
            context.put(DslPath.INSTANCE, _dslPath);
            context.put(Download.INSTANCE);
            //context.put(Dependencies.INSTANCE, "temp");
            //context.put(Prompt.INSTANCE, null);
            context.put(Force.INSTANCE);
            context.put(LogOutput.INSTANCE  );

            List<CompileParameter> params = Main.initializeParameters(context, ".");

            if(!Main.processContext(context, params)) {
                throw new MojoExecutionException(context.errorLog.toString());
            }

            context.close();

        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

}
