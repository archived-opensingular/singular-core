package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class STypeDateTest {

    SDictionary dict = SDictionary.create();

    @Test public void storesDateInISOFormat(){
        SIDate d = newInstance(dict);

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue(reference.toDate());
        assertThat(d.toStringPersistence()).isEqualTo("2016-01-01");
    }

    @Test public void displaysDateInLatinFormat(){
        SIDate d = newInstance(dict);

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue(reference.toDate());
        assertThat(d.toStringDisplayDefault()).isEqualTo("01/01/2016");
    }

    @Test public void selectLabelIsInLatinFormat(){
        SIDate d = newInstance(dict);

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue(reference.toDate());
        assertThat(d.toStringDisplayDefault()).isEqualTo("01/01/2016");
    }

    @Test public void convertsFromISOForrmat(){
        SIDate d = newInstance(dict);

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue("2016-01-01");
        assertThat(d.getValue()).isEqualTo(reference.toDate());
    }

    @Test public void convertsLatinForrmat(){
        SIDate d = newInstance(dict);

        DateTime reference = DateTime.parse("2016-01-01");
        d.setValue("01/01/2016");
        assertThat(d.getValue()).isEqualTo(reference.toDate());
    }

    @Ignore
    @Test(expected = Exception.class) public void rejectsNotStandartFormart(){
        newInstance(dict).setValue("2016/01/01");
    }

    private SIDate newInstance(final SDictionary dict) {
        return (SIDate) SDocumentFactory.empty().createInstance(new RefType() {
                @Override
                protected SType<?> retrieve() {
                    return dict.getType(STypeDate.class);
                }
            });
    }

}
