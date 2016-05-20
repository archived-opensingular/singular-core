package br.net.mirante.singular.studio.core.example;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;

@SInfoType(name = "Order", spackage = SPackageOrder.class)
public class STypeOrder extends STypeComposite<SIComposite> {

    public STypeInteger id;
    public STypeString description;
    public STypeList<STypeItem, SIComposite> itens;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        id = addFieldInteger("id");
        description = addFieldString("descricao");
        itens = addFieldListOf("itens", STypeItem.class);
    }
}
