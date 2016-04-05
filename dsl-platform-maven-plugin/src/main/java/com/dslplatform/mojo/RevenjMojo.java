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

    public void setDslPath(String dslPath) throws MojoExecutionException {
        this._dslPath = Utils.resourceAbsolutePath(dslPath);
    }

    public void execute()
        throws MojoExecutionException
    {
        try {
            MojoContext context = new MojoContext();

            context.put(Targets.INSTANCE, "revenj.java");
            context.put(DslPath.INSTANCE, _dslPath);
            context.put(Download.INSTANCE, null);
            //context.put(Dependencies.INSTANCE, "temp");
            //context.put(Prompt.INSTANCE, null);
            context.put(Force.INSTANCE, null);
            context.put(LogOutput.INSTANCE, null);

            List<CompileParameter> params = Main.initializeParameters(context, ".");

            if(!Main.processContext(context, params)) {
                throw new MojoExecutionException(context.errorLog.toString());
            } else {
                System.out.println(context.showLog.toString());
                System.out.println(context.errorLog.toString());
                System.out.println(context.traceLog.toString());
            }

            context.close();

        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

}
