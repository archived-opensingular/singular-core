package br.net.mirante.singular.bamclient.builder;

import junit.framework.TestCase;

public class AmSerialChartBuilderTest extends TestCase {

    public void testName() throws Exception {
        SingularAmChartBuilder builder = new SingularAmChartBuilder();
        String json = builder.newSerialChart().theme("light").finish();
        assertTrue(json.equals("{\"type\":\"serial\",\"theme\":\"light\"}"));
    }

}