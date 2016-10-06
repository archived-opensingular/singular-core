package org.opensingular.singular.form;

import java.util.Arrays;

import org.opensingular.singular.bam.form.FilterFieldType;
import org.junit.Test;

import org.opensingular.singular.bamclient.portlet.filter.FieldType;
import static org.opensingular.singular.bam.form.FilterFieldType.valueOfFieldType;
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