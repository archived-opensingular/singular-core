package br.net.mirante.singular.form.type.core;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocument;
import br.net.mirante.singular.form.document.SDocumentFactoryEmpty;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

import static org.fest.assertions.api.Assertions.assertThat;

public class InitListenerTest {

    SDictionary dict = SDictionary.create();
    private STypeComposite<SIComposite> base;
    private STypeString                 field1, field2;

    @Before
    public void setup() {
        PackageBuilder test = dict.createNewPackage("test");
        base = test.createCompositeType("base");
        field1 = base.addFieldString("field1");
        field2 = base.addFieldString("field2");
    }

    @Test
    public void runIntializationCode() {
        field1.withInitListener((x) -> {
            x.setValue("abacate");
        });

        assertThat(newInstance(field1).getValue()).isEqualTo("abacate");
    }

    @Test
    public void intializationCodeHasAccessToAllServices() {
        field1.withInitListener((x) -> {
            assertThat(x.getDocument()).isNotNull();
            assertThat(x.getDocument().lookupService(P.class)).isNotNull();
            x.setValue("abacate");
        });

        assertThat(newInstance(field1).getValue()).isEqualTo("abacate");
    }

    @Test
    public void intializationIsRunnedForAllInstances() {
        field1.withInitListener((x) -> {
            x.setValue("abacate");
        });
        field2.withInitListener((x) -> {
            x.setValue("avocado");
        });

        SIComposite ins = (SIComposite) newInstance(base);
        assertThat(ins.getValue(field1)).isEqualTo("abacate");
        assertThat(ins.getValue(field2)).isEqualTo("avocado");
    }

    private static class P implements Serializable {

    }

    private SInstance newInstance(SType t) {
        return new SDocumentFactoryEmpty() {
            @Override
            protected void setupDocument(SDocument document) {
                super.setupDocument(document);
                document.bindLocalService("test", P.class, RefService.of(new P()));
            }
        }.createInstance(new RefType() {
            @Override
            protected SType<?> retrieve() {
                return t;
            }
        });

    }

}
