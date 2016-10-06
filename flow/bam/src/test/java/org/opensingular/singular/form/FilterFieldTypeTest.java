package org.opensingular.singular.form;

import java.util.Arrays;

import com.opensingular.bam.form.FilterFieldType;
import org.junit.Test;

import com.opensingular.bam.client.portlet.filter.FieldType;
import static com.opensingular.bam.form.FilterFieldType.valueOfFieldType;
import static org.junit.Assert.assertTrue;


public class FilterFieldTypeTest {

    @Test
    public void testIfContainsFieldTypes() {

        final String msg = "Não existe uma representação de %s em "+FilterFieldType.class.getName();

        Arrays.asList(FieldType.values())
                .stream()
                .filter(ft -> !ft.equals(FieldType.DEFAULT))
                .forEach(ft -> assertTrue(String.format(msg, ft), valueOfFieldType(ft).isPresent()));
    }

}