package org.opensingular.singular.form.showcase.component.studio;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypeString;

@SInfoType(name = "Item", spackage = SPackageOrder.class)
public class STypeItem extends STypeComposite<SIComposite> {

    public STypeInteger id;
    public STypeString descricao;
    public STypeMonetary cost;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        id = addFieldInteger("id");
        id.asAtr().label("Id");
        descricao = addFieldString("descricao");
        descricao.asAtr().label("Descrição");
        cost = addFieldMonetary("cost");
    }
}
