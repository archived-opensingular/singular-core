package org.opensingular.singular.portlet.chart;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.opensingular.bam.client.builder.amchart.AmChartValueField;
import com.opensingular.bam.client.chart.AreaChart;
import com.opensingular.bam.client.chart.ColumnSerialChart;
import com.opensingular.bam.client.chart.DonutPieChart;
import com.opensingular.bam.client.chart.LineSerialChart;
import com.opensingular.bam.client.chart.PieChart;
import com.opensingular.bam.client.chart.SingularChart;
import com.opensingular.bam.spring.ObjectMapperFactory;
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
