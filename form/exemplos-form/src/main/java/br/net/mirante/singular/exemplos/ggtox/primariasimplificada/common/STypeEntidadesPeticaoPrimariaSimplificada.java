package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeEntidadesPeticaoPrimariaSimplificada extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeRequerente         requerente         = addField("requerente", STypeRequerente.class);
        final STypeRepresentanteLegal representanteLegal = addField("representanteLegal", STypeRepresentanteLegal.class);

    }
}
