package br.net.mirante.singular.form.view;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestViewResolver2 extends SPackage{

    private STypeComposite<SIComposite> something;

    @SInfoType(spackage = TestViewResolver2.class)
    public static class StypeSomething extends STypeComposite<SIComposite> {

        @Override
        protected void onLoadType(TypeBuilder tb) {
            super.onLoadType(tb);
            this.setView(SViewAutoComplete::new);
        }
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.createType(StypeSomething.class);
        STypeComposite<SIComposite> baseType = pb.createCompositeType("baseType");
        something = baseType.addField("something",StypeSomething.class);
    }

    @Test
    public void returnBaseTypeView() throws Exception {
        SDictionary dict = SDictionary.create();
        PackageBuilder pkg = dict.createNewPackage("test");
        STypeComposite<SIComposite> baseType = pkg.createCompositeType("baseType");
        STypeComposite<SIComposite> something = baseType.addFieldComposite("something");

        something.autocomplete();

        assertEquals(SViewAutoComplete.class,
                ViewResolver.resolve(something.newInstance()).getClass());
    }

    @Test
    public void returnTypeViewWhenItsFromSuperType() throws Exception {
        SDictionary dict = SDictionary.create();
        TestViewResolver2 pkg = dict.loadPackage(TestViewResolver2.class);
        assertEquals(SViewAutoComplete.class,
                ViewResolver.resolve(pkg.something.newInstance()).getClass());
    }

}
