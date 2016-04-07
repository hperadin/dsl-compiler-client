package com.dslplatform.mojo;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class MojosTest extends AbstractMojoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGenerateCode()
            throws Exception {
        File pom = getTestFile( "src/test/resources/generate-code-pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        GenerateCodeMojo myMojo = (GenerateCodeMojo) lookupMojo(GenerateCodeMojo.GOAL, pom);
        assertNotNull( myMojo );

        myMojo.execute();
    }

    public void testApplyMigration()
            throws Exception {
        File pom = getTestFile( "src/test/resources/apply-migration-pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        ApplyMigrationMojo myMojo = (ApplyMigrationMojo) lookupMojo(ApplyMigrationMojo.GOAL, pom);
        assertNotNull( myMojo );

        myMojo.execute();
    }

}