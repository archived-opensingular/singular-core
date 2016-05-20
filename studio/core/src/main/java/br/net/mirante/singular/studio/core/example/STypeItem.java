package br.net.mirante.singular.studio.core.example;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeMonetary;
import br.net.mirante.singular.form.type.core.STypeString;

@SInfoType(name = "Item", spackage = SPackageOrder.class)
public class STypeItem extends STypeComposite<SIComposite> {

    public STypeInteger id;
    public STypeString description;
    public STypeMonetary cost;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        id = addFieldInteger("id");
        description = addFieldString("descricao");
        cost = addFieldMonetary("cost");
    }
}
