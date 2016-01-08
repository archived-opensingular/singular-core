package br.net.mirante.singular.bamclient.builder;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class SingularAmChartBuilderTest extends TestCase {

    public void testFinish() throws Exception {
        SingularAmChartBuilder builder = new SingularAmChartBuilder();
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

        ChartDataProvider provider = new ChartDataProvider();
        provider.addData(map1);
        provider.addData(map2);

        String json = new SingularAmChartBuilder().newSerialChart()
                .dataProvider(provider).finish();

        assertEquals("{\"type\":\"serial\",\"dataProvider\":[{\"idade\":\"24\",\"nome\":\"danilo\"},{\"idade\":\"26\",\"nome\":\"rodrigo\"}]}", json);

    }
}