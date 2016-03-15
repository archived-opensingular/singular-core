package br.net.mirante.singular.form.mform.document;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.TestCaseForm;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.SPackageCore;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.event.ISInstanceListener;
import br.net.mirante.singular.form.mform.event.SInstanceAttributeChangeEvent;
import br.net.mirante.singular.form.mform.event.SInstanceEventType;
import br.net.mirante.singular.form.mform.event.SInstanceValueChangeEvent;
import org.junit.Assert;
import org.junit.Before;

public class TestSDocumentEvents extends TestCaseForm {

    private SDictionary dicionario;
    private SIString root;
    private SDocument   doc;

    private ISInstanceListener.EventCollector globalCollector;
    private ISInstanceListener.EventCollector attributeCollector;
    private ISInstanceListener.EventCollector valueCollector;

    @Before
    public void setUp() {
        dicionario = SDictionary.create();
        root = dicionario.newInstance(STypeString.class);
        doc = root.getDocument();

        globalCollector = new ISInstanceListener.EventCollector();
        attributeCollector = new ISInstanceListener.EventCollector(e -> e instanceof SInstanceAttributeChangeEvent);
        valueCollector = new ISInstanceListener.EventCollector(e -> e instanceof SInstanceValueChangeEvent);
    }

    public void testValueChanges() {
        doc.getInstanceListeners().add(SInstanceEventType.VALUE_CHANGED, globalCollector);

        root.setValue("ABC");
        assertEventsCount(1, globalCollector);

        root.setValue("ABC");
        assertEventsCount(1, globalCollector);

        root.setValue("CCC");
        assertEventsCount(2, globalCollector);
    }

    public void testAttributeChanges() {
        doc.getInstanceListeners().add(SInstanceEventType.ATTRIBUTE_CHANGED, attributeCollector);

        root.setAttributeValue(SPackageCore.ATR_REQUIRED, true);
        assertEventsCount(1, attributeCollector);

        root.setAttributeValue(SPackageCore.ATR_REQUIRED, true);
        assertEventsCount(1, attributeCollector);

        root.setAttributeValue(SPackageCore.ATR_REQUIRED, false);
        assertEventsCount(2, attributeCollector);
    }

    public void testValueAndAttributeChanges() {
        doc.getInstanceListeners().add(SInstanceEventType.values(), globalCollector);
        doc.getInstanceListeners().add(SInstanceEventType.ATTRIBUTE_CHANGED, attributeCollector);
        doc.getInstanceListeners().add(SInstanceEventType.VALUE_CHANGED, valueCollector);

        root.setValue("ABC");
        assertEventsCount(1, globalCollector);
        assertEventsCount(0, attributeCollector);
        assertEventsCount(1, valueCollector);

        root.setValue("CCC");
        assertEventsCount(2, globalCollector);
        assertEventsCount(0, attributeCollector);
        assertEventsCount(2, valueCollector);

        root.setAttributeValue(SPackageCore.ATR_REQUIRED, true);
        assertEventsCount(3, globalCollector);
        assertEventsCount(1, attributeCollector);
        assertEventsCount(2, valueCollector);

        root.setAttributeValue(SPackageCore.ATR_REQUIRED, false);
        assertEventsCount(4, globalCollector);
        assertEventsCount(2, attributeCollector);
        assertEventsCount(2, valueCollector);
    }

    private static void assertEventsCount(int expected, ISInstanceListener.EventCollector collector) {
        Assert.assertEquals(expected, collector.getEvents().size());
    }
}
