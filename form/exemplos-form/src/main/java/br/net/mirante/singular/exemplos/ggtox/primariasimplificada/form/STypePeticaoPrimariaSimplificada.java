package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(name = "STypePeticaoPrimariaSimplificada", spackage = SPackagePeticaoPrimariaSimplificada.class)
public class STypePeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        asAtr().label("Petição primaria Simplificada")
                .displayString("Petição de ${tipoPeticao.nome}, nível ${nivel}");
        SPackagePeticaoPrimariaSimplificada.onLoadType(this);
    }
}
