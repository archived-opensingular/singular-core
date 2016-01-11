package br.net.mirante.singular.bamclient.builder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.net.mirante.singular.bamclient.chart.ChartDataProvider;
import br.net.mirante.singular.bamclient.chart.StalessChartDataProvider;
import br.net.mirante.singular.bamclient.portlet.PortletFilterContext;
import junit.framework.TestCase;

public class SingularChartBuilderTest extends TestCase {

    public void testFinish() throws Exception {
        SingularChartBuilder builder = new SingularChartBuilder();
        String result = builder.newSerialChart().finish();
        assertTrue("{\"type\":\"serial\"}".equals(result));
    }


    public void testSetDataProvider() throws Exception {

        Map<String, String> map1 = new HashMap<>();
        Map<String, String> map2 = new HashMap<>();

        map1.put("nome", "danilo");
        map1.put("idade", "24");
        map2.put("nome", "rodrigo");
        map2.put("idade", "26");

        ChartDataProvider provider = new StalessChartDataProvider() {
            @Override
            public List<Map<String, String>> loadData(PortletFilterContext filterContext) {
                return Arrays.asList(map1, map2);
            }
        };

        String json = new SingularChartBuilder().newSerialChart()
                .dataProvider(provider, new PortletFilterContext()).finish();

        assertEquals("{\"type\":\"serial\",\"dataProvider\":[{\"idade\":\"24\",\"nome\":\"danilo\"},{\"idade\":\"26\",\"nome\":\"rodrigo\"}]}", json);

    }
}