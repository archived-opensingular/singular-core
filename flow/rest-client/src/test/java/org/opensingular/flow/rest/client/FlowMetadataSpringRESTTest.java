package org.opensingular.flow.rest.client;

import org.junit.Assert;
import org.junit.Test;


public class FlowMetadataSpringRESTTest {

    @Test
    public void testaddOtherParameters() throws Exception {
        final FlowMetadataSpringREST flowMetadataSpringREST = new FlowMetadataSpringREST(null, null);
        final String result = flowMetadataSpringREST.addOtherParameters("danilo", "idade", "ok");
        Assert.assertEquals(result, "&danilo={danilo}&idade={idade}&ok={ok}");

        final String result2 = flowMetadataSpringREST.addOtherParameters();
        Assert.assertEquals(result2, "");

        final String result3 = flowMetadataSpringREST.addOtherParameters((String[]) null);
        Assert.assertEquals(result3, null);
    }
}