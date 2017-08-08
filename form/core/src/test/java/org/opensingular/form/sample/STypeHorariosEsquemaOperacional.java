package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;

@SInfoType(spackage = AntaqPackage.class, newable = false, name = "HorariosEsquemaOperacional")
public class STypeHorariosEsquemaOperacional extends STypeComposite<SIComposite> {

    public STypePortoLocalHorario partida;
    public STypePortoLocalHorario chegada;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        partida = this.addField("partida", STypePortoLocalHorario.class);
        chegada = this.addField("chegada", STypePortoLocalHorario.class);

        partida.asAtr().label("Partida");
        chegada.asAtr().label("Chegada");
        this.asAtrAnnotation().setAnnotated();


    }
}
