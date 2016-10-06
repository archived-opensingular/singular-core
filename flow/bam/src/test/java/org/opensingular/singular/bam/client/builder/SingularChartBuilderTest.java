package org.opensingular.singular.bam.client.builder;

import com.opensingular.bam.client.builder.SingularChartBuilder;
import junit.framework.TestCase;

public class SingularChartBuilderTest extends TestCase {

    public void testFinish() throws Exception {
        SingularChartBuilder builder = new SingularChartBuilder();
        String result = builder.newSerialChart().finish();
        assertTrue("{\"type\":\"serial\"}".equals(result));
    }

}