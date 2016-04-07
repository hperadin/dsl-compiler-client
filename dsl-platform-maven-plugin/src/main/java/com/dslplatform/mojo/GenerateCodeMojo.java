package com.dslplatform.mojo;

import com.dslplatform.compiler.client.CompileParameter;
import com.dslplatform.compiler.client.Main;
import com.dslplatform.compiler.client.parameters.*;
import com.dslplatform.mojo.context.MojoContext;
import com.dslplatform.mojo.utils.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.dslplatform.mojo.utils.Utils.*;
import static org.apache.commons.io.FileUtils.copyDirectory;

@Mojo(name = GenerateCodeMojo.GOAL)
public class GenerateCodeMojo extends AbstractMojo {

    public static final String GOAL = "generate-code";

    @Parameter(defaultValue = "src/generated/java")
    private String generatedSourcesTarget;

    @Parameter(defaultValue = "src/main/resources/META-INF/services")
    private String servicesManifestTarget;

    @Parameter(property = "target")
    private String target_;

    @Parameter(property = "dsl")
    private String dsl_;

    private Targets.Option targetParsed;
    private Map<CompileParameter, String> compileParametersParsed = new HashMap<CompileParameter, String>();
    private Map<Settings.Option, String> flagsParsed = new HashMap<Settings.Option, String>();

    public void setTarget(String target) {
        if(target == null) return;
        this.target_ = target;
        this.targetParsed = Utils.targetOptionFrom(target);
    }

    public void setDsl(String dsl) {
        if(dsl == null) return;
        this.dsl_ = dsl;
        compileParametersParsed.put(DslPath.INSTANCE, dsl);
    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        cleanupParameters(this.compileParametersParsed);
        // TODO: Default values
        sanitizeDirectories(this.compileParametersParsed);

        MojoContext context = new MojoContext(getLog())
                .with(this.targetParsed)
                .with(this.flagsParsed)
                .with(this.compileParametersParsed)
                .with(Force.INSTANCE)
                .with(Download.INSTANCE)
                .with(Prompt.INSTANCE)
                .with(Settings.Option.SOURCE_ONLY)
                ;

        List<CompileParameter> params = Main.initializeParameters(context, ".");

        if(!Main.processContext(context, params)) {
            throw new MojoExecutionException(context.errorLog.toString());
        } else {
            // Copy generated sources
            copyGeneratedSources(context);
            registerServices(context);
        }

        context.close();
    }

    protected void registerServices(MojoContext context) throws MojoExecutionException {
        try {
            String namespace = context.get(Namespace.INSTANCE);
            String service = namespace == null ? "Boot" : namespace + ".Boot";
            Utils.createDirIfNotExists(this.servicesManifestTarget);
            File servicesRegistration = new File(servicesManifestTarget, "org.revenj.extensibility.SystemAspect");
            FileUtils.write(servicesRegistration, service, "UTF-8");
        } catch (IOException e){
            throw new MojoExecutionException("Error writing the services registration file.", e);
        }
    }

    private void copyGeneratedSources(MojoContext context) throws MojoExecutionException {
        File tmpPath = TempPath.getTempProjectPath(context);
        File generatedSources = new File(tmpPath.getAbsolutePath(), targetParsed.name());
        try {
            createDirIfNotExists(this.generatedSourcesTarget);
            copyDirectory(generatedSources, new File(this.generatedSourcesTarget));
        } catch (IOException e) {
            throw new MojoExecutionException("Error copying the generated sources", e);
        }
    }
}
