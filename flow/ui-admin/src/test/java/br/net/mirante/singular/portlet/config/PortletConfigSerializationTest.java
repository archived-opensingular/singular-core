package br.net.mirante.singular.portlet.config;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.net.mirante.singular.bamclient.chart.PieChart;
import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.MorrisChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.spring.ObjectMapperFactory;
import junit.framework.TestCase;

public class PortletConfigSerializationTest extends TestCase {

    private ObjectMapper mapper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mapper = new ObjectMapperFactory().getObjectMapper();
    }

    public void testAmChartPortletConfig() throws IOException {
        final PortletConfig<?> config = new AmChartPortletConfig("http://xxx.xx.xx", new PieChart("b", "x"));
        final String originalConfigSerialized = mapper.writeValueAsString(config);
        final PortletConfig<?> originalConfigDeserialized = mapper.readValue(originalConfigSerialized, PortletConfig.class);
        final String newConfigSerialized = mapper.writeValueAsString(originalConfigDeserialized);
        assertEquals(originalConfigSerialized, newConfigSerialized);
    }

    public void testMorrisChartPortletConfig() throws IOException {
        final PortletConfig<?> config = new MorrisChartPortletConfig("http://xxx.xx.xx", new PieChart("b", "x"));
        final String originalConfigSerialized = mapper.writeValueAsString(config);
        final PortletConfig<?> originalConfigDeserialized = mapper.readValue(originalConfigSerialized, PortletConfig.class);
        final String newConfigSerialized = mapper.writeValueAsString(originalConfigDeserialized);
        assertEquals(originalConfigSerialized, newConfigSerialized);
    }

}
