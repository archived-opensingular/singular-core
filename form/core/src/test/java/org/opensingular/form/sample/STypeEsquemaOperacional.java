package org.opensingular.form.sample;

import org.jetbrains.annotations.NotNull;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.view.SViewListByMasterDetail;

import java.util.Optional;

@SInfoType(spackage = AntaqPackage.class, newable = false, name = "EsquemaOperacional")
public class STypeEsquemaOperacional extends STypeComposite<SIComposite> {


    public STypeEmbarcacaoEsquemaOperacional                       embarcacaoEsquemaOperacional;
    public STypeList<STypeHorariosEsquemaOperacional, SIComposite> horariosTarifas;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onLoadType(@NotNull TypeBuilder tb) {

        embarcacaoEsquemaOperacional = this.addField("embarcacaoEsquemaOperacional", STypeEmbarcacaoEsquemaOperacional.class);
        embarcacaoEsquemaOperacional.selection()
                .id(embarcacaoEsquemaOperacional.identificador)
                .display(embarcacaoEsquemaOperacional.nome)
                .simpleProvider(builder -> {
                    findEmbarcacoesList(builder.getCurrentInstance()).forEach(embarcacao -> {
                        builder.add()
                                .set(embarcacaoEsquemaOperacional.identificador, embarcacao.findField(STypeEmbarcacao.class, e -> e.identificador).map(SInstance::getValue).orElse(null))
                                .set(embarcacaoEsquemaOperacional.nome, embarcacao.findField(STypeEmbarcacao.class, e -> e.nome).map(SInstance::getValue).orElse(null));
                    });
                });
        embarcacaoEsquemaOperacional
                .asAtr()
                .dependsOn(STypeEmbarcacoes.class, sTypeEmbarcacoes -> sTypeEmbarcacoes.embarcacoes);


        horariosTarifas = this.addFieldListOf("horariosTarifas", STypeHorariosEsquemaOperacional.class);
        horariosTarifas.withView(
                new SViewListByMasterDetail()
                        .col("Partida", "${_inst.partida.pais!''} - ${_inst.partida.uf!''} - ${_inst.partida.municipio!''} - ${_inst.partida.localAtracacao!''} - ${_inst.partida.diaSemana!''} - ${_inst.partida.horario!''}")
                        .col("Chegada", "${_inst.chegada.pais!''} - ${_inst.chegada.uf!''} - ${_inst.chegada.municipio!''} - ${_inst.chegada.localAtracacao!''} - ${_inst.chegada.diaSemana!''} - ${_inst.chegada.horario!''}")
        );

        this
                .asAtr()
                .displayString("Esquema para Embarcação: ${(embarcacaoEsquemaOperacional.nome)!}")
                .dependsOn(embarcacaoEsquemaOperacional)
                .asAtrAnnotation()
                .setAnnotated();

    }

    @SuppressWarnings("unchecked")
    private SIList<SIComposite> findEmbarcacoesList(SInstance instance) {
        Optional<SIComposite> embarcacoes = instance.findNearest(STypeEmbarcacoes.class);
        if (embarcacoes.isPresent()) {
            return (SIList<SIComposite>) embarcacoes.get().getField(STypeEmbarcacoes.EMBARCACOES_FIELD_NAME);
        }
        return null;
    }

}
