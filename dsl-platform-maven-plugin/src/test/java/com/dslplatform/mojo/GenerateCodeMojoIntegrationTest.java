package com.dslplatform.mojo;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

import static com.dslplatform.mojo.TestUtils.assertDir;
import static com.dslplatform.mojo.TestUtils.assertFile;

@RunWith(JUnit4.class)
public class GenerateCodeMojoIntegrationTest extends AbstractMojoTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGenerateCode()
            throws Exception {
        File pom = getTestFile( "src/test/resources/generate-code-pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        GenerateCodeMojo mojo = (GenerateCodeMojo) lookupMojo(GenerateCodeMojo.GOAL, pom);
        assertNotNull(mojo);
        mojo.execute();

        String sourcesPath = mojo.getGeneratedSourcesTarget();
        assertDir(sourcesPath);
        assertDir(sourcesPath+"/MojoTestModule");
        assertFile(sourcesPath+"/MojoTestModule/MojoTestAggregate.java");
        assertDir(sourcesPath+"/MojoTestModule/converters");
        assertFile(sourcesPath+"/MojoTestModule/converters/MojoTestAggregateConverter.java");
        assertFile(sourcesPath+"/Boot.java");

        File servicesDir = new File(mojo.getServicesManifestTarget());
        assertDir(servicesDir.getAbsolutePath());

        File servicesFile = new File(servicesDir, "org.revenj.extensibility.SystemAspect");
        assertFile(servicesFile.getAbsolutePath());

        String namespace = mojo.getNamespace();
        assertEquals(namespace != null ? namespace + ".Boot" : "Boot", FileUtils.readFileToString(servicesFile));


    }

}