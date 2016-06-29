package br.net.mirante.singular.form.document;

import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.TestCaseForm;
import br.net.mirante.singular.form.event.ISInstanceListener;
import br.net.mirante.singular.form.event.SInstanceAttributeChangeEvent;
import br.net.mirante.singular.form.event.SInstanceEventType;
import br.net.mirante.singular.form.event.SInstanceValueChangeEvent;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.STypeString;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.function.Supplier;

@RunWith(Parameterized.class)
public class TestSDocumentEvents extends TestCaseForm {

    private SDictionary dictionary;
    private SIString    root;
    private SDocument   doc;

    private ISInstanceListener.EventCollector globalCollector;
    private ISInstanceListener.EventCollector attributeCollector;
    private ISInstanceListener.EventCollector valueCollector;

    public TestSDocumentEvents(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setUp() {
        dictionary = createTestDictionary();
        root = dictionary.newInstance(STypeString.class);
        doc = root.getDocument();

        globalCollector = new ISInstanceListener.EventCollector();
        attributeCollector = new ISInstanceListener.EventCollector(e -> e instanceof SInstanceAttributeChangeEvent);
        valueCollector = new ISInstanceListener.EventCollector(e -> e instanceof SInstanceValueChangeEvent);
    }

    @Test
    public void testValueChanges() {
        doc.getInstanceListeners().add(SInstanceEventType.VALUE_CHANGED, globalCollector);

        root.setValue("ABC");
        assertEventsCount(1, globalCollector);

        root.setValue("ABC");
        assertEventsCount(1, globalCollector);

        root.setValue("CCC");
        assertEventsCount(2, globalCollector);
    }

    @Test
    public void testAttributeChanges() {
        doc.getInstanceListeners().add(SInstanceEventType.ATTRIBUTE_CHANGED, attributeCollector);

        root.setAttributeValue(SPackageBasic.ATR_REQUIRED, true);
        assertEventsCount(1, attributeCollector);

        root.setAttributeValue(SPackageBasic.ATR_REQUIRED, true);
        assertEventsCount(1, attributeCollector);

        root.setAttributeValue(SPackageBasic.ATR_REQUIRED, false);
        assertEventsCount(2, attributeCollector);
    }

    @Test
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

        root.setAttributeValue(SPackageBasic.ATR_REQUIRED, true);
        assertEventsCount(3, globalCollector);
        assertEventsCount(1, attributeCollector);
        assertEventsCount(2, valueCollector);

        root.setAttributeValue(SPackageBasic.ATR_REQUIRED, false);
        assertEventsCount(4, globalCollector);
        assertEventsCount(2, attributeCollector);
        assertEventsCount(2, valueCollector);
    }

    private static void assertEventsCount(int expected, ISInstanceListener.EventCollector collector) {
        Assert.assertEquals(expected, collector.getEvents().size());
    }
}
