package br.net.mirante.singular.bamclient.portlet.filter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import br.net.mirante.singular.bamclient.portlet.FilterConfig;
import br.net.mirante.singular.flow.core.DashboardFilter;
import static org.junit.Assert.assertTrue;

public class FilterConfigFactoryTest {

    @Test
    public void testCreateConfigForClass() throws Exception {
        final List<FilterConfig> configs = FilterConfigFactory.createConfigForClass(DummyClass.class);
        final List<Field> fields = Arrays.asList(DummyClass.class.getDeclaredFields());
        assertTrue(configs.size() == fields.size());

        final Map<String, FieldType> fieldTypeMap = new HashMap<>();

        fieldTypeMap.put("textField", FieldType.TEXT);
        fieldTypeMap.put("textAreaField", FieldType.TEXT);
        fieldTypeMap.put("integerField", FieldType.INTEGER);
        fieldTypeMap.put("dateField", FieldType.DATE);
        fieldTypeMap.put("selectionField", FieldType.SELECTION);
        fieldTypeMap.put("aggregationPeriodField", FieldType.AGGREGATION_PERIOD);

        fields.forEach(f -> {
            final Optional<FilterConfig> config = configs
                    .stream()
                    .filter(fc -> fc.getIdentifier().equals(f.getName()))
                    .findFirst();
            assertTrue(config.isPresent());
            assertTrue(fieldTypeMap.get(f.getName()).equals(config.get().getFieldType()));
        });

    }
}

class DummyClass implements DashboardFilter {

    @FilterField(label = "textField")
    String textField;

    @FilterField(label = "textAreaField")
    String textAreaField;

    @FilterField(label = "integerField")
    Integer integerField;

    @FilterField(label = "dateField")
    Date dateField;

    @FilterField(label = "selectionField", type = FieldType.SELECTION, options = {"a", "b", "c"})
    String selectionField;

    @FilterField(label = "aggregationPeriodField")
    AggregationPeriod aggregationPeriodField;
}
