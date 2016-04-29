package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class STypeDateTimeTest {
    SDictionary dict = SDictionary.create();

    @Test
    public void storesDateInISOFormat(){
        SIDateTime d = newInstance(dict);

        DateTime reference = DateTime.parse("2016-01-01T05:21:33.000-02:00");
        d.setValue(reference.toDate());
        assertThat(d.toStringPersistence()).isEqualTo("2016-01-01T07:21:33.000+00:00");
    }

    @Test public void displaysDateInLatinFormat(){
        SIDateTime d = newInstance(dict);

        DateTime reference = DateTime.parse("2016-01-01T05:21:33.000");
        d.setValue(reference.toDate());
        assertThat(d.toStringDisplayDefault()).isEqualTo("01/01/2016 05:21");
    }

    @Test public void selectLabelIsInLatinFormat(){
        SIDateTime d = newInstance(dict);

        DateTime reference = DateTime.parse("2016-01-01T05:21:33.000");
        d.setValue(reference.toDate());
        assertThat(d.toStringDisplayDefault()).isEqualTo("01/01/2016 05:21");
    }

    @Test public void convertsFromISOForrmat(){
        SIDateTime d = newInstance(dict);

        DateTime reference = DateTime.parse("2016-01-01T05:21:33.000-02:00");
        d.setValue("2016-01-01T05:21:33.000-02:00");
        assertThat(d.getValue()).isEqualTo(reference.toDate());
    }

    @Test public void convertsLatinForrmat(){
        SIDateTime d = newInstance(dict);

        DateTime reference = DateTime.parse("2016-01-01T05:21:00.000");
        d.setValue("01/01/2016 05:21");
        assertThat(d.getValue()).isEqualTo(reference.toDate());
    }

    private SIDateTime newInstance(final SDictionary dict) {
        return (SIDateTime) SDocumentFactory.empty().createInstance(new RefType() {
            @Override
            protected SType<?> retrieve() {
                return dict.getType(STypeDateTime.class);
            }
        });
    }
}
