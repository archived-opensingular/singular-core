package br.net.mirante.singular.portlet.chart;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.net.mirante.singular.bamclient.builder.amchart.AmChartValueField;
import br.net.mirante.singular.bamclient.chart.AreaChart;
import br.net.mirante.singular.bamclient.chart.ColumnSerialChart;
import br.net.mirante.singular.bamclient.chart.DonutPieChart;
import br.net.mirante.singular.bamclient.chart.LineSerialChart;
import br.net.mirante.singular.bamclient.chart.PieChart;
import br.net.mirante.singular.bamclient.chart.SingularChart;
import br.net.mirante.singular.bam.spring.ObjectMapperFactory;
import junit.framework.TestCase;

public class SingularChartSerializationTest extends TestCase {

    private ObjectMapper mapper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mapper = new ObjectMapperFactory().getObjectMapper();
    }

    public void testColumnSerialChart() throws IOException {
        final SingularChart originalChart = new ColumnSerialChart("x", new AmChartValueField("a", "b", "c"));
        final String originalChartSerialized = mapper.writeValueAsString(originalChart);
        final SingularChart deserializedChart = mapper.readValue(originalChartSerialized, SingularChart.class);
        final String deseriaziledChartSerialized = mapper.writeValueAsString(deserializedChart);
        assertEquals(originalChartSerialized, deseriaziledChartSerialized);
    }

    public void testLineSerialChart() throws IOException {
        final SingularChart originalChart = new LineSerialChart("x", new AmChartValueField("a", "b", "c"));
        final String originalChartSerialized = mapper.writeValueAsString(originalChart);
        final SingularChart deserializedChart = mapper.readValue(originalChartSerialized, SingularChart.class);
        final String deseriaziledChartSerialized = mapper.writeValueAsString(deserializedChart);
        assertEquals(originalChartSerialized, deseriaziledChartSerialized);
    }

    public void testPieChart() throws IOException {
        final SingularChart originalChart = new PieChart("b", "a");
        final String originalChartSerialized = mapper.writeValueAsString(originalChart);
        final SingularChart deserializedChart = mapper.readValue(originalChartSerialized, SingularChart.class);
        final String deseriaziledChartSerialized = mapper.writeValueAsString(deserializedChart);
        assertEquals(originalChartSerialized, deseriaziledChartSerialized);
    }

    public void testDonutPieChart() throws IOException {
        final SingularChart originalChart = new DonutPieChart("b", "a");
        final String originalChartSerialized = mapper.writeValueAsString(originalChart);
        final SingularChart deserializedChart = mapper.readValue(originalChartSerialized, SingularChart.class);
        final String deseriaziledChartSerialized = mapper.writeValueAsString(deserializedChart);
        assertEquals(originalChartSerialized, deseriaziledChartSerialized);
    }

    public void testAreaChart() throws IOException {
        final SingularChart originalChart = new AreaChart("a", "b");
        final String originalChartSerialized = mapper.writeValueAsString(originalChart);
        final SingularChart deserializedChart = mapper.readValue(originalChartSerialized, SingularChart.class);
        final String deseriaziledChartSerialized = mapper.writeValueAsString(deserializedChart);
        assertEquals(originalChartSerialized, deseriaziledChartSerialized);
    }
}
