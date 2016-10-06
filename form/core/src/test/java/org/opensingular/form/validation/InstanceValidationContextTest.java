package org.opensingular.form.validation;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.validation.InstanceValidationContext;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.STypeString;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


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

        PackageBuilder pkt = createTestDictionary().createNewPackage("teste");
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