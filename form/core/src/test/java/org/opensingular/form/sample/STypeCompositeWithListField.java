package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.view.SViewByBlock;

@SInfoType(spackage = FormTestPackage.class, newable = false, name = "STypeCompositeWithListField")
public class STypeCompositeWithListField extends STypeComposite<SIComposite> {


    public static final String EMBARCACOES_FIELD_NAME = "theList";
    public STypeList<STypeFirstListElement, SIComposite> theList;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        theList = this.addFieldListOf(EMBARCACOES_FIELD_NAME, STypeFirstListElement.class);
        theList.withMiniumSizeOf(1);
        theList.withInitListener(list -> list.addNew());
        theList.asAtr().label("Embarcações");

        this.withView(new SViewByBlock(), v -> v.newBlock("Foo Foo").add(theList));
    }
}
