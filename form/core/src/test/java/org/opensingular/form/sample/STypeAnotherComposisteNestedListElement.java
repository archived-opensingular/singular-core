package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;

@SInfoType(spackage = FormTestPackage.class, newable = false, name = "STypeAnotherComposisteNestedListElement")
public class STypeAnotherComposisteNestedListElement extends STypeComposite<SIComposite> {

    public STypeFooData partida;
    public STypeFooData chegada;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        partida = this.addField("partida", STypeFooData.class);
        chegada = this.addField("chegada", STypeFooData.class);

        partida.asAtr().label("Partida");
        chegada.asAtr().label("Chegada");
        this.asAtrAnnotation().setAnnotated();


    }
}
