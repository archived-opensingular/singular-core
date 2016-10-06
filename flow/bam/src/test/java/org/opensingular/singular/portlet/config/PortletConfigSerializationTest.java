package org.opensingular.singular.portlet.config;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.opensingular.bam.client.chart.PieChart;
import com.opensingular.bam.client.portlet.AmChartPortletConfig;
import com.opensingular.bam.client.portlet.DataEndpoint;
import com.opensingular.bam.client.portlet.MorrisChartPortletConfig;
import com.opensingular.bam.client.portlet.PortletConfig;
import com.opensingular.bam.spring.ObjectMapperFactory;
import junit.framework.TestCase;

public class PortletConfigSerializationTest extends TestCase {

    private ObjectMapper mapper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mapper = new ObjectMapperFactory().getObjectMapper();
    }

    public void testAmChartPortletConfig() throws IOException {
        final PortletConfig<?> config = new AmChartPortletConfig(DataEndpoint.local("http://xxx.xx.xx"), new PieChart("b", "x"));
        final String originalConfigSerialized = mapper.writeValueAsString(config);
        final PortletConfig<?> originalConfigDeserialized = mapper.readValue(originalConfigSerialized, PortletConfig.class);
        final String newConfigSerialized = mapper.writeValueAsString(originalConfigDeserialized);
        assertEquals(originalConfigSerialized, newConfigSerialized);
    }

    public void testMorrisChartPortletConfig() throws IOException {
        final PortletConfig<?> config = new MorrisChartPortletConfig(DataEndpoint.local("http://xxx.xx.xx"), new PieChart("b", "x"));
        final String originalConfigSerialized = mapper.writeValueAsString(config);
        final PortletConfig<?> originalConfigDeserialized = mapper.readValue(originalConfigSerialized, PortletConfig.class);
        final String newConfigSerialized = mapper.writeValueAsString(originalConfigDeserialized);
        assertEquals(originalConfigSerialized, newConfigSerialized);
    }

}
