package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.view.SViewTab;
import org.opensingular.lib.commons.base.SingularProperties;

@SInfoType(spackage = FormTestPackage.class, name = "STypeFormTest")
public class STypeFormTest extends STypeComposite<SIComposite> {//NOSONAR


    public final static boolean OBRIGATORIO       = !SingularProperties.get().isTrue(SingularProperties.SINGULAR_DEV_MODE);
    public final static int     QUANTIDADE_MINIMA = OBRIGATORIO ? 1 : 0;

    public STypeCompositeWithListField compositeWithListField;
    public STypeAnotherComposite       anotherComposite;


    @Override
    protected void onLoadType(TypeBuilder tb) {

        this.asAtr().label("Foo")
                .displayString("Bar");


        compositeWithListField = this.addField("compositeWithListField", STypeCompositeWithListField.class);
        anotherComposite = this.addField("anotherComposite", STypeAnotherComposite.class);


        SViewTab tabbed = new SViewTab();

        tabbed.addTab("compositeWithListField", "Embarcações").add(compositeWithListField);
        tabbed.addTab("anexoCA", "Esquema Operacional").add(anotherComposite);
        withView(tabbed);


    }
}

