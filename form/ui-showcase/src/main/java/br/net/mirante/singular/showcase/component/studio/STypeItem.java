package br.net.mirante.singular.showcase.component.studio;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeMonetary;
import br.net.mirante.singular.form.type.core.STypeString;

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
