package com.dslplatform.mojo;

import com.dslplatform.compiler.client.CompileParameter;
import com.dslplatform.compiler.client.Main;
import com.dslplatform.compiler.client.parameters.*;
import com.dslplatform.mojo.context.MojoContext;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.*;

import static com.dslplatform.mojo.Utils.*;

@Mojo(name="execute")
public class RevenjMojo
    extends AbstractMojo {

    @Parameter(property = "targets")
    private Map<String, String> targets_;

    @Parameter(property="settings")
    private String[] settings_;

    @Parameter(property = "compileParameters")
    private Map<String, String> compileParameters_;

    private Map<Targets.Option, String> targetsParsed;
    private List<Settings.Option> settingsParsed;
    private Map<CompileParameter, String> compileParametersParsed;

    private final Utils utils;
    public RevenjMojo() {
        this.utils = new Utils(getLog());
    }

    public void setTargets(Map<String, String> targets) {
        this.targets_ = targets;
        this.targetsParsed = new HashMap<Targets.Option, String>();
        for(Map.Entry<String, String> kv: targets.entrySet()) {
            String key = kv.getKey();
            String value = kv.getValue();

            Targets.Option option = targetOptionFrom(key);
            if(option != null) targetsParsed.put(option, value);
        }
    }

    public void setSettings(String[] settings) {
        this.settings_ = settings;
        this.settingsParsed = new ArrayList<Settings.Option>(settings.length);
        for(String setting: settings) {

            Settings.Option option = settingsOptionFrom(setting);
            if(option != null) settingsParsed.add(option);
        }
    }

    public void setCompileParameters(Map<String, String> compileParameters) {
        this.compileParameters_ = compileParameters;
        this.compileParametersParsed = new HashMap<CompileParameter, String>();
        for(Map.Entry<String, String> kv: compileParameters.entrySet()) {
            String key = kv.getKey();
            String value = kv.getValue();

            CompileParameter compileParameter = compileParameterFrom(key);
            if(compileParameter != null) compileParametersParsed.put(compileParameter, value);
        }
    }

    public void execute()
        throws MojoExecutionException
    {
        getLog().info("Compile parameters:");
        write(this.compileParameters_);
        getLog().info("Targets:");
        write(this.targets_);
        getLog().info("Settings:");
        write(Arrays.asList(this.settings_));

        getLog().info("Compile parameters parsed:");
        write(this.compileParametersParsed);
        getLog().info("Targets parsed:");
        write(this.targetsParsed);
        getLog().info("Settings parsed:");
        write(this.settingsParsed);

        createDirectoriesIfNeeded();
        MojoContext context = new MojoContext(getLog())
            .with(this.targetsParsed)
            .with(this.compileParametersParsed)
            .with(this.settingsParsed)
            .with(Force.INSTANCE)
            .with(Download.INSTANCE)
            .with(Prompt.INSTANCE)
            ;

        List<CompileParameter> params = Main.initializeParameters(context, ".");

        if(!Main.processContext(context, params)) {
            System.out.println(context.errorLog.toString());
            System.out.println(context.showLog.toString());
            System.out.println(context.traceLog.toString());
            throw new MojoExecutionException(context.errorLog.toString());
        }

        context.close();
    }

    private void createDirectoriesIfNeeded() {
        for(Map.Entry<CompileParameter, String> kv : compileParametersParsed.entrySet()) {
            CompileParameter cp = kv.getKey();
            String value = kv.getValue();

            if(cp instanceof TempPath) {
                this.compileParametersParsed.put(cp, utils.createDirIfNotExists(value));
            }

            if(cp instanceof DslPath) {
                this.compileParametersParsed.put(cp, utils.resourceAbsolutePath(value));
            }
        }
    }

    private <K,V> void write(Map<K,V> map) {
        for(Map.Entry kv : map.entrySet()){
            getLog().info(kv.getKey().toString() + " : " + kv.getValue());
        }
    }

    private <K> void write(List<K> list) {
        getLog().info(list.toString());
    }

}
