package org.opensingular.form.type.core;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.AbstractTestOneType;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class STypeDateTimeTest extends AbstractTestOneType<STypeDateTime, SIDateTime> {

    public STypeDateTimeTest(TestFormConfig testFormConfig) {
        super(testFormConfig, STypeDateTime.class);
    }

    @Test
    public void storesDateInISOFormat(){
        SIDateTime d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01T05:21:33.000-02:00");
        d.setValue(reference.toDate());
        assertThat(d.toStringPersistence()).isEqualTo("2016-01-01T07:21:33.000+00:00");
    }

    @Test public void displaysDateInLatinFormat(){
        SIDateTime d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01T05:21:33.000");
        d.setValue(reference.toDate());
        assertThat(d.toStringDisplayDefault()).isEqualTo("01/01/2016 05:21");
    }

    @Test public void selectLabelIsInLatinFormat(){
        SIDateTime d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01T05:21:33.000");
        d.setValue(reference.toDate());
        assertThat(d.toStringDisplayDefault()).isEqualTo("01/01/2016 05:21");
    }

    @Test public void convertsFromISOForrmat(){
        SIDateTime d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01T05:21:33.000-02:00");
        d.setValue("2016-01-01T05:21:33.000-02:00");
        assertThat(d.getValue()).isEqualTo(reference.toDate());
    }

    @Test public void convertsLatinForrmat(){
        SIDateTime d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01T05:21:00.000");
        d.setValue("01/01/2016 05:21");
        assertThat(d.getValue()).isEqualTo(reference.toDate());
    }
}
