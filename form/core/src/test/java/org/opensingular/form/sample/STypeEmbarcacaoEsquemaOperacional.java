package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;

@SInfoType(spackage = AntaqPackage.class, newable = false, name = "EmbarcacaoEsquemaOperacional")
public class STypeEmbarcacaoEsquemaOperacional extends STypeComposite<SIComposite> {

    public STypeString identificador;
    public STypeString nome;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        this.addFieldString("identificador");
        this.addFieldString("nome");
    }
}
