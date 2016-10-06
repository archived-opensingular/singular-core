package br.net.mirante.singular.form.type.core;

import br.net.mirante.singular.form.AbstractTestOneType;
import org.opensingular.singular.form.type.core.SIDate;
import org.opensingular.singular.form.type.core.STypeDate;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class STypeDateTest extends AbstractTestOneType<STypeDate, SIDate> {

    public STypeDateTest(TestFormConfig testFormConfig) {
        super(testFormConfig, STypeDate.class);
    }

    @Test public void storesDateInISOFormat(){
        SIDate d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue(reference.toDate());
        assertThat(d.toStringPersistence()).isEqualTo("2016-01-01");
    }

    @Test public void displaysDateInLatinFormat(){
        SIDate d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue(reference.toDate());
        assertThat(d.toStringDisplayDefault()).isEqualTo("01/01/2016");
    }

    @Test public void selectLabelIsInLatinFormat(){
        SIDate d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue(reference.toDate());
        assertThat(d.toStringDisplayDefault()).isEqualTo("01/01/2016");
    }

    @Test public void convertsFromISOForrmat(){
        SIDate d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue("2016-01-01");
        assertThat(d.getValue()).isEqualTo(reference.toDate());
    }

    @Test public void convertsLatinForrmat(){
        SIDate d = newInstance();

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue("01/01/2016");
        assertThat(d.getValue()).isEqualTo(reference.toDate());
    }

    @Ignore
    @Test(expected = Exception.class) public void rejectsNotStandartFormart(){
        newInstance().setValue("2016/01/01");
    }
}
