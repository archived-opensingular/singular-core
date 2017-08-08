package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.view.SViewByBlock;

@SInfoType(spackage = AntaqPackage.class, newable = false, name = "Embarcacacoes")
public class STypeEmbarcacoes extends STypeComposite<SIComposite> {


    public static final String EMBARCACOES_FIELD_NAME = "embarcacoes";
    public STypeList<STypeEmbarcacao, SIComposite> embarcacoes;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        embarcacoes = this.addFieldListOf(EMBARCACOES_FIELD_NAME, STypeEmbarcacao.class);
        embarcacoes.withMiniumSizeOf(1);
        embarcacoes.withInitListener(list -> list.addNew());
        embarcacoes.asAtr().label("Embarcações");

        this.withView(new SViewByBlock(), v -> v.newBlock("Dados das embarcações").add(embarcacoes));
    }
}
