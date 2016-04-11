package com.dslplatform.mojo;

import com.dslplatform.compiler.client.CompileParameter;
import com.dslplatform.compiler.client.Main;
import com.dslplatform.compiler.client.parameters.*;
import com.dslplatform.mojo.context.MojoContext;
import com.dslplatform.mojo.utils.Utils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mojo(name = GenerateCodeMojo.GOAL)
public class GenerateCodeMojo extends AbstractMojo {

	public static final String GOAL = "generate-code";

	private static final String SERVICES_FILE = "org.revenj.extensibility.SystemAspect";

	@Parameter
	private MavenProject project;

	@Parameter(defaultValue = "target/generated-sources")
	private String generatedSourcesTarget;

	@Parameter(defaultValue = "target/classes/META-INF/services")
	private String servicesManifestTarget;

	@Parameter(name = "target", property = "target", required = true)
	private String compileTarget;

	@Parameter(name = "dsl", property = "dsl", defaultValue = "dsl")
	private String dslPath;

	@Parameter(name = "namespace", property = "namespace", defaultValue="")
	private String namespaceString;

	private Targets.Option targetParsed;
	private Map<CompileParameter, String> compileParametersParsed = new HashMap<CompileParameter, String>();
	private Map<Settings.Option, String> flagsParsed = new HashMap<Settings.Option, String>();

	public String getGeneratedSourcesTarget() {
		return generatedSourcesTarget;
	}

	public String getServicesManifestTarget() {
		return servicesManifestTarget;
	}

	public String getTarget() {
		return compileTarget;
	}

	public String getDsl() {
		return dslPath;
	}

	public String getNamespace() {
		return this.namespaceString;
	}

	public void setTarget(String target) {
		System.out.println("Target: " + target);
		if (target == null) return;
		this.compileTarget = target;
		this.targetParsed = Utils.targetOptionFrom(target);
		System.out.println("Target: " + targetParsed);
	}

	public void setDsl(String dsl) {
		if (dsl == null) return;
		this.dslPath = dsl;
		compileParametersParsed.put(DslPath.INSTANCE, dsl);
	}

	public void setNamespace(String namespace) {
		this.namespaceString = namespace;
		compileParametersParsed.put(Namespace.INSTANCE, namespace);
	}

	public void execute() throws MojoExecutionException, MojoFailureException {
		Utils.cleanupParameters(this.compileParametersParsed);
		// TODO: Default values
		Utils.sanitizeDirectories(this.compileParametersParsed);

		MojoContext context = new MojoContext(getLog())
				.with(this.targetParsed)
				.with(this.flagsParsed)
				.with(this.compileParametersParsed)
				.with(Force.INSTANCE)
				.with(Download.INSTANCE)
				.with(Prompt.INSTANCE)
				.with(Settings.Option.SOURCE_ONLY);

		List<CompileParameter> params = Main.initializeParameters(context, ".");

		if (!Main.processContext(context, params)) {
			throw new MojoExecutionException(context.errorLog.toString());
		} else {
			// Copy generated sources
			copyGeneratedSources(context);
			registerServices(context);
			// This supposedly adds generated sources to maven compile classpath:
			//project.addCompileSourceRoot(this.generatedSourcesTarget);
		}

		context.close();
	}

	protected void registerServices(MojoContext context) throws MojoExecutionException {
		String namespace = context.get(Namespace.INSTANCE);
		String service = namespace == null ? "Boot" : namespace + ".Boot";
		Utils.createDirIfNotExists(this.servicesManifestTarget);
		File servicesRegistration = new File(servicesManifestTarget, SERVICES_FILE);
		Utils.writeToFile(servicesRegistration, service, "UTF-8");
	}

	private void copyGeneratedSources(MojoContext context) throws MojoExecutionException {
		File tmpPath = TempPath.getTempProjectPath(context);
		File generatedSources = new File(tmpPath.getAbsolutePath(), targetParsed.name());
		Utils.createDirIfNotExists(this.generatedSourcesTarget);
		Utils.copyFolder(generatedSources, new File(this.generatedSourcesTarget), context);
	}


}
