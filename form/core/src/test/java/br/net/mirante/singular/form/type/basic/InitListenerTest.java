package br.net.mirante.singular.form.type.basic;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.STypeString;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Pelo que observei, o init listener só
 * é utilizando quando a instancia é criada por {@link br.net.mirante.singular.form.document.SDocumentFactory#createInstance(RefType)}
 */
@Ignore
@RunWith(Parameterized.class)
public class InitListenerTest extends TestCaseForm {

    public InitListenerTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testIfSimpleInitWithValue() {

        final PackageBuilder pb   = createTestDictionary().createNewPackage("SPackageTest");
        final STypeString    nome = pb.createType("nome", STypeString.class);

        nome.withInitListener(si -> si.setValue("banana"));

        final SIString iFruta = nome.newInstance();

        assertEquals("banana", iFruta.getValue());

    }

    @Test
    public void testIfCompositeInitWithValue() {

        final PackageBuilder              pb   = createTestDictionary().createNewPackage("SPackageTest");
        final STypeComposite<SIComposite> root = pb.createCompositeType("root");
        final STypeString                 nome = root.addField("nome", STypeString.class);

        nome.withInitListener(si -> si.setValue("banana"));

        final SIComposite newInstance = root.newInstance();
        final SIString    iFruta      = (SIString) newInstance.getField("nome");

        assertEquals("banana", iFruta.getValue());

    }

    @Test
    public void testIfListItemInitWithValue() {

        final PackageBuilder                                      pb     = createTestDictionary().createNewPackage("SPackageTest");
        final STypeComposite<SIComposite>                         root   = pb.createCompositeType("root");
        final STypeList<STypeComposite<SIComposite>, SIComposite> frutas = root.addFieldListOfComposite("frutas", "fruta");
        final STypeComposite<SIComposite>                         fruta  = frutas.getElementsType();
        final STypeString                                         nome   = fruta.addField("nome", STypeString.class);

        nome.withInitListener(si -> si.setValue("banana"));


        final SIComposite         newInstance = root.newInstance();
        final SIList<SIComposite> iFrutas     = (SIList<SIComposite>) newInstance.getField("frutas");
        final SIComposite         newFruta    = iFrutas.addNew();

        assertEquals("banana", newFruta.getValue());

    }

}


