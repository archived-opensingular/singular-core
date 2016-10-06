package org.opensingular.singular.bamclient.builder;

import junit.framework.TestCase;

public class AmSerialChartBuilderTest extends TestCase {

    public void testName() throws Exception {
        SingularChartBuilder builder = new SingularChartBuilder();
        String json = builder.newSerialChart().theme("light").finish();
        assertTrue(json.equals("{\"type\":\"serial\",\"theme\":\"light\"}"));
    }

}