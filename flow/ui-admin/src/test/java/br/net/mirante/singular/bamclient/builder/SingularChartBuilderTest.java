package br.net.mirante.singular.bamclient.builder;

import junit.framework.TestCase;

public class SingularChartBuilderTest extends TestCase {

    public void testFinish() throws Exception {
        SingularChartBuilder builder = new SingularChartBuilder();
        String result = builder.newSerialChart().finish();
        assertTrue("{\"type\":\"serial\"}".equals(result));
    }

}