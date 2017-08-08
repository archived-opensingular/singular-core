package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.view.SViewTab;
import org.opensingular.lib.commons.base.SingularProperties;

@SInfoType(spackage = AntaqPackage.class, name = "InteriorPassageirosCargasLong")
public class Resolucao912Form extends STypeComposite<SIComposite> {


    public final static boolean OBRIGATORIO       = !SingularProperties.get().isTrue(SingularProperties.SINGULAR_DEV_MODE);
    public final static int     QUANTIDADE_MINIMA = OBRIGATORIO ? 1 : 0;

    public STypeEmbarcacoes          embarcacoes;
    public STypeEsquemasOperacionais esquemaOperacionalAnexo;


    @Override
    protected void onLoadType(TypeBuilder tb) {

        this.asAtr().label("Interior - Resolução 912 Passageiros e Carga no Longitudinal")
                .displayString("Res. 912 - Nav. Interior - Passageiros e Carga  Longitudinal: ${(dadosEmpresa.empresa.nomeFantasia)!} - (${(dadosEmpresa.empresa.cnpj)!})");



        embarcacoes = this.addField("embarcacoes", STypeEmbarcacoes.class);
        esquemaOperacionalAnexo = this.addField("esquemaOperacionalAnexo", STypeEsquemasOperacionais.class);


        SViewTab tabbed = new SViewTab();

        tabbed.addTab("embarcacoes", "Embarcações").add(embarcacoes);
        tabbed.addTab("anexoCA", "Esquema Operacional").add(esquemaOperacionalAnexo);
        withView(tabbed);


    }
}

