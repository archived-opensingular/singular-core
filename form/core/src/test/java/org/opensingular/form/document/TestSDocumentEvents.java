/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.document;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SDictionary;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.event.ISInstanceListener;
import org.opensingular.form.event.SInstanceAttributeChangeEvent;
import org.opensingular.form.event.SInstanceEventType;
import org.opensingular.form.event.SInstanceValueChangeEvent;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;

@RunWith(Parameterized.class)
public class TestSDocumentEvents extends TestCaseForm {

    private SDictionary dictionary;
    private SIString    root;
    private SDocument doc;

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



    @Test
    public void testRemoveListener() {
        doc.getInstanceListeners().add(SInstanceEventType.ATTRIBUTE_CHANGED, globalCollector);
        doc.getInstanceListeners().add(SInstanceEventType.ATTRIBUTE_CHANGED, globalCollector);
        doc.getInstanceListeners().add(SInstanceEventType.ATTRIBUTE_CHANGED, globalCollector);

        Assert.assertEquals(3, getSizeOfListeners(SInstanceEventType.ATTRIBUTE_CHANGED));

        doc.getInstanceListeners().remove(SInstanceEventType.ATTRIBUTE_CHANGED, globalCollector);

        Assert.assertEquals(2, getSizeOfListeners(SInstanceEventType.ATTRIBUTE_CHANGED));

        doc.getInstanceListeners().add(SInstanceEventType.VALUE_CHANGED, globalCollector);
        doc.getInstanceListeners().add(SInstanceEventType.VALUE_CHANGED, globalCollector);

        Assert.assertEquals(2, getSizeOfListeners(SInstanceEventType.VALUE_CHANGED));

        SInstanceEventType[] eventTypes = new SInstanceEventType[] {SInstanceEventType.VALUE_CHANGED, SInstanceEventType.ATTRIBUTE_CHANGED};
        doc.getInstanceListeners().remove(eventTypes, globalCollector);

        Assert.assertEquals(1, getSizeOfListeners(SInstanceEventType.VALUE_CHANGED));
        Assert.assertEquals(1, getSizeOfListeners(SInstanceEventType.ATTRIBUTE_CHANGED));

    }

    private int getSizeOfListeners(SInstanceEventType valueChanged) {
        return doc.getInstanceListeners().getInstanceListeners(valueChanged).size();
    }

    private static void assertEventsCount(int expected, ISInstanceListener.EventCollector collector) {
        Assert.assertEquals(expected, collector.getEvents().size());
    }
}
