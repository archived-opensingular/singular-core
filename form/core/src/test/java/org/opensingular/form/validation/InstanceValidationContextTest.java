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

package org.opensingular.form.validation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.STypeString;


@RunWith(Parameterized.class)
public class InstanceValidationContextTest extends TestCaseForm {

    InstanceValidationContext   context;
    STypeComposite<SIComposite> root;
    STypeComposite<SIComposite> node_1;
    STypeComposite<SIComposite> node_2;
    STypeString                 leaf_1;
    STypeString                 leaf_2;
    SIComposite                 iRoot;

    public InstanceValidationContextTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setUp() {
        context = new InstanceValidationContext();

        PackageBuilder pkt = createTestPackage();
        root = pkt.createCompositeType("root");
        node_1 = root.addFieldComposite("node_1");
        node_2 = root.addFieldComposite("node_2");
        leaf_1 = node_1.addField("leaf_1", STypeString.class);
        leaf_2 = node_2.addField("leaf_2", STypeString.class);

        leaf_1.asAtr().required();
        leaf_2.asAtr().required();
        node_1.addInstanceValidator(i -> i.error(""));
        node_2.addInstanceValidator(i -> i.error(""));

        iRoot = root.newInstance();
    }

    @Test
    public void assertThatContainsTwoErrors() {
        context.validateAll(iRoot);
        Assert.assertEquals(2, iRoot.getDocument().getValidationErrors().size());
    }

    @Test
    public void assertThatContainsTwoErrorsAfterSettingSomeValueOnLeafOfNode_1() {
        iRoot.findNearest(leaf_1).ifPresent(i -> i.setValue("X"));
        context.validateAll(iRoot);
        Assert.assertEquals(2, iRoot.getDocument().getValidationErrors().size());
    }

    @Test
    public void assertThatContainsTwoErrorsAfterSettingSomeValueOnLeafOfNode_2() {
        iRoot.findNearest(leaf_2).ifPresent(i -> i.setValue("X"));
        context.validateAll(iRoot);
        Assert.assertEquals(2, iRoot.getDocument().getValidationErrors().size());
    }

    @Test
    public void assertThatContainsTwoErrorsAfterSettingSomeValueOnLeafOfNode_1AndNode_2() {
        iRoot.findNearest(leaf_1).ifPresent(i -> i.setValue("X"));
        iRoot.findNearest(leaf_2).ifPresent(i -> i.setValue("X"));
        context.validateAll(iRoot);
        Assert.assertEquals(2, iRoot.getDocument().getValidationErrors().size());
    }

}