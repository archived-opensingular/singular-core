package br.net.mirante.singular.form.mform;

import org.junit.Assert;
import org.junit.Before;

import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MPacoteCore;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.event.IMInstanceListener;
import br.net.mirante.singular.form.mform.event.MInstanceAttributeChangeEvent;
import br.net.mirante.singular.form.mform.event.MInstanceValueChangeEvent;

public class TestSDocumentEvents extends TestCaseForm {

    private MDicionario dicionario;
    private MIString    root;
    private SDocument   doc;

    private IMInstanceListener.EventCollector globalCollector;
    private IMInstanceListener.EventCollector attributeCollector;
    private IMInstanceListener.EventCollector valueCollector;

    @Before
    public void setUp() {
        dicionario = MDicionario.create();
        root = dicionario.novaInstancia(MTipoString.class);
        doc = root.getDocument();

        globalCollector = new IMInstanceListener.EventCollector();
        attributeCollector = new IMInstanceListener.EventCollector(e -> e instanceof MInstanceAttributeChangeEvent);
        valueCollector = new IMInstanceListener.EventCollector(e -> e instanceof MInstanceValueChangeEvent);
    }

    public void testValueChanges() {
        doc.addInstanceListener(globalCollector);

        root.setValor("ABC");
        assertEventsCount(1, globalCollector);

        root.setValor("ABC");
        assertEventsCount(1, globalCollector);

        root.setValor("CCC");
        assertEventsCount(2, globalCollector);
    }

    public void testAttributeChanges() {
        doc.addInstanceListener(attributeCollector);

        root.setValorAtributo(MPacoteCore.ATR_OBRIGATORIO, true);
        assertEventsCount(1, attributeCollector);

        root.setValorAtributo(MPacoteCore.ATR_OBRIGATORIO, true);
        assertEventsCount(1, attributeCollector);

        root.setValorAtributo(MPacoteCore.ATR_OBRIGATORIO, false);
        assertEventsCount(2, attributeCollector);
    }

    public void testValueAndAttributeChanges() {
        doc.addInstanceListener(globalCollector);
        doc.addInstanceListener(attributeCollector);
        doc.addInstanceListener(valueCollector);

        root.setValor("ABC");
        assertEventsCount(1, globalCollector);
        assertEventsCount(0, attributeCollector);
        assertEventsCount(1, valueCollector);

        root.setValor("CCC");
        assertEventsCount(2, globalCollector);
        assertEventsCount(0, attributeCollector);
        assertEventsCount(2, valueCollector);

        root.setValorAtributo(MPacoteCore.ATR_OBRIGATORIO, true);
        assertEventsCount(3, globalCollector);
        assertEventsCount(1, attributeCollector);
        assertEventsCount(2, valueCollector);

        root.setValorAtributo(MPacoteCore.ATR_OBRIGATORIO, false);
        assertEventsCount(4, globalCollector);
        assertEventsCount(2, attributeCollector);
        assertEventsCount(2, valueCollector);
    }

    private static void assertEventsCount(int expected, IMInstanceListener.EventCollector collector) {
        Assert.assertEquals(expected, collector.getEvents().size());
    }
}
