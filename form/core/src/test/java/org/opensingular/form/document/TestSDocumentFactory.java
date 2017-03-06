/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.document;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;

import java.util.List;

/**
 * @author Daniel C. Bordin on 26/02/2017.
 */
public class TestSDocumentFactory {

    private static int callCount;

    @Before
    public void setUp() {
        callCount = 0;
    }

    @Test
    public void testEmptyFactory() {
        SDocumentFactory factory = SDocumentFactory.empty();
        assertTreeCalls(factory, Lists.newArrayList(0, 0, 0), Lists.newArrayList(null, null, null));
    }

    @Test
    public void testFactoryExtensionWithNull() {
        SDocumentFactory factory = SDocumentFactory.empty();
        Assert.assertSame(factory, factory.extendAddingSetupStep(null));
    }

    @Test
    public void testSDocumentConsumerExtensionWithNull() {
        SDocumentConsumer consumer = SDocumentConsumer.of(TestSDocumentFactory::increment1);
        Assert.assertSame(consumer, consumer.extendWith(null));
    }

    @Test
    public void testFactoryWithOneExtensionSetupStepExtendingEmptyFavtory() {
        SDocumentFactory factory = SDocumentFactory.empty().extendAddingSetupStep(TestSDocumentFactory::increment1);
        assertTreeCalls(factory, Lists.newArrayList(1, 2, 3), Lists.newArrayList("1", "1", "1"));
    }

    @Test
    public void testFactoryWithOneExtensionSetupStepUsingStaticConstructor() {
        SDocumentFactory factory = SDocumentFactory.of(TestSDocumentFactory::increment1);
        assertTreeCalls(factory, Lists.newArrayList(1, 2, 3), Lists.newArrayList("1", "1", "1"));
    }

    @Test
    public void testFactoryWithTwoExtensionSetupStepUsingStaticConstructor() {
        SDocumentFactory factory = SDocumentFactory.of(TestSDocumentFactory::increment1);
        factory = factory.extendAddingSetupStep(TestSDocumentFactory::increment2);
        assertTreeCalls(factory, Lists.newArrayList(2, 4, 6), Lists.newArrayList("12", "12", "12"));
    }

    @Test
    public void testFactoryWithExtensionUsing_SDocumentSetuper() {
        SDocumentFactory factory = SDocumentFactory.of(TestSDocumentFactory::increment1);
        SDocumentConsumer setuper = SDocumentConsumer.of(TestSDocumentFactory::increment2).extendWith(
                TestSDocumentFactory::increment3);
        factory = factory.extendAddingSetupStep(setuper);
        assertTreeCalls(factory, Lists.newArrayList(3, 6, 9), Lists.newArrayList("123", "123", "123"));
    }

    private void assertTreeCalls(SDocumentFactory factory, List<Integer> expectedCount, List<String> expectedValue) {
        assertCallsCount(0);
        AssertionsSInstance aInstance = createWithFactory(factory);
        assertCallsCount(expectedCount.get(0));
        aInstance.isValueEquals(expectedValue.get(0));

        aInstance = aInstance.serializeAndDeserialize();
        assertCallsCount(expectedCount.get(1));
        aInstance.isValueEquals(expectedValue.get(1));

        aInstance = aInstance.serializeAndDeserialize();
        assertCallsCount(expectedCount.get(2));
        aInstance.isValueEquals(expectedValue.get(2));
    }

    private static void increment1(SDocument document) {
        incrementInt(document, '1');
    }

    private static void increment2(SDocument document) {
        incrementInt(document, '2');
    }

    private static void increment3(SDocument document) {
        incrementInt(document, '3');
    }

    private static void incrementInt(SDocument document, char increment) {
        callCount++;
        SIString root = (SIString) document.getRoot();
        String v = root.toStringPersistence();
        v = v == null ? Character.toString(increment) : v + increment;
        //This call should make a difference only on the first call. The other one will be overridy by the XML value
        // from the serialziation
        root.setValue(v);
    }

    private static void assertCallsCount(int expectedCallCount) {
        Assert.assertEquals(expectedCallCount, callCount);
    }

    private AssertionsSInstance createWithFactory(SDocumentFactory factory) {
        RefType refType = RefType.of(STypeString.class);
        return new AssertionsSInstance(factory.createInstance(refType)).is(SIString.class);
    }
}
