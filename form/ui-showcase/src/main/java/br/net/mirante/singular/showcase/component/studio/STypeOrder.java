package br.net.mirante.singular.showcase.component.studio;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeString;

@SInfoType(name = "Order", spackage = SPackageOrder.class)
public class STypeOrder extends STypeComposite<SIComposite> {

    public STypeInteger id;
    public STypeString descricao;
    public STypeList<STypeItem, SIComposite> itens;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        id = addFieldInteger("id");
        id.asAtr().label("Id");
        descricao = addFieldString("descricao");
        descricao
                .asAtr()
                .label("Descrição")
                .required();
        itens = addFieldListOf("itens", STypeItem.class);
    }
}
