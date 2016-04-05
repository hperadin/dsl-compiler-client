package com.dslplatform.mojo;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;

public class RevenjMojoTest extends AbstractMojoTestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSomething()
            throws Exception
    {
        File pom = getTestFile( "src/test/resources/pom.xml" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        RevenjMojo myMojo = (RevenjMojo) lookupMojo( "revenj_java", pom );
        assertNotNull( myMojo );
        myMojo.execute();
    }

}