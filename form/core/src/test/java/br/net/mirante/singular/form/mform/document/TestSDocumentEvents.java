package br.net.mirante.singular.form.mform.document;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.TestCaseForm;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.event.IMInstanceListener;
import br.net.mirante.singular.form.mform.event.SInstanceAttributeChangeEvent;
import br.net.mirante.singular.form.mform.event.MInstanceEventType;
import br.net.mirante.singular.form.mform.event.SInstanceValueChangeEvent;
import org.junit.Assert;
import org.junit.Before;

public class TestSDocumentEvents extends TestCaseForm {

    private SDictionary dicionario;
    private SIString root;
    private SDocument   doc;

    private IMInstanceListener.EventCollector globalCollector;
    private IMInstanceListener.EventCollector attributeCollector;
    private IMInstanceListener.EventCollector valueCollector;

    @Before
    public void setUp() {
        dicionario = SDictionary.create();
        root = dicionario.novaInstancia(STypeString.class);
        doc = root.getDocument();

        globalCollector = new IMInstanceListener.EventCollector();
        attributeCollector = new IMInstanceListener.EventCollector(e -> e instanceof SInstanceAttributeChangeEvent);
        valueCollector = new IMInstanceListener.EventCollector(e -> e instanceof SInstanceValueChangeEvent);
    }

    public void testValueChanges() {
        doc.getInstanceListeners().add(MInstanceEventType.VALUE_CHANGED, globalCollector);

        root.setValor("ABC");
        assertEventsCount(1, globalCollector);

        root.setValor("ABC");
        assertEventsCount(1, globalCollector);

        root.setValor("CCC");
        assertEventsCount(2, globalCollector);
    }

    public void testAttributeChanges() {
        doc.getInstanceListeners().add(MInstanceEventType.ATTRIBUTE_CHANGED, attributeCollector);

        root.setValorAtributo(SPackageCore.ATR_OBRIGATORIO, true);
        assertEventsCount(1, attributeCollector);

        root.setValorAtributo(SPackageCore.ATR_OBRIGATORIO, true);
        assertEventsCount(1, attributeCollector);

        root.setValorAtributo(SPackageCore.ATR_OBRIGATORIO, false);
        assertEventsCount(2, attributeCollector);
    }

    public void testValueAndAttributeChanges() {
        doc.getInstanceListeners().add(MInstanceEventType.values(), globalCollector);
        doc.getInstanceListeners().add(MInstanceEventType.ATTRIBUTE_CHANGED, attributeCollector);
        doc.getInstanceListeners().add(MInstanceEventType.VALUE_CHANGED, valueCollector);

        root.setValor("ABC");
        assertEventsCount(1, globalCollector);
        assertEventsCount(0, attributeCollector);
        assertEventsCount(1, valueCollector);

        root.setValor("CCC");
        assertEventsCount(2, globalCollector);
        assertEventsCount(0, attributeCollector);
        assertEventsCount(2, valueCollector);

        root.setValorAtributo(SPackageCore.ATR_OBRIGATORIO, true);
        assertEventsCount(3, globalCollector);
        assertEventsCount(1, attributeCollector);
        assertEventsCount(2, valueCollector);

        root.setValorAtributo(SPackageCore.ATR_OBRIGATORIO, false);
        assertEventsCount(4, globalCollector);
        assertEventsCount(2, attributeCollector);
        assertEventsCount(2, valueCollector);
    }

    private static void assertEventsCount(int expected, IMInstanceListener.EventCollector collector) {
        Assert.assertEquals(expected, collector.getEvents().size());
    }
}
