package br.net.mirante.singular.form.wicket.test;

import java.util.function.Supplier;

import org.junit.Before;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;

public abstract class AbstractWicketFormTest {

    protected SDictionary dicionario;

    @Before
    public void setUpDicionario() {
        dicionario = SDictionary.create();
    }

    protected static SInstance createIntance(Supplier<SType<?>> typeSupplier) {
        RefType ref = new RefType() {
            @Override
            protected SType<?> retrieve() {
                return typeSupplier.get();
            }
        };
        return SDocumentFactory.empty().createInstance(ref);
    }

}
