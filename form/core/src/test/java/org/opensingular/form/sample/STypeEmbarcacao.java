package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewListByMasterDetail;

import java.util.Optional;
import java.util.UUID;

@SInfoType(spackage = AntaqPackage.class, newable = false, name = "Embarcacao")
public class STypeEmbarcacao extends STypeComposite<SIComposite> {

    public STypeString                                     identificador;
    public STypeString                                     nome;
    public STypeString                                     inscricao;
    //    public STypeDate                                       validade;
    public STypeString                                     tipoEmbarcacao;
    public STypeList<STypeHabilitacaoTecnica, SIComposite> habilitacaoTecnicaList;
    public STypeString                                     naturezaTransporte;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        identificador = this.addFieldString("identificador");
        identificador
                .asAtr()
                .visible(false);
        identificador.withInitListener(si -> si.setValue(UUID.randomUUID().toString()));

        nome = this.addFieldString("nome");
        nome.asAtr()
                .required(Resolucao912Form.OBRIGATORIO)
                .label("Nome da Embarcação")
                .asAtrBootstrap()
                .colPreference(3);

        inscricao = this.addFieldString("inscricao");
        inscricao
                .asAtr()
                .required(Resolucao912Form.OBRIGATORIO)
                .label("Número de Inscrição").asAtrBootstrap()
                .colPreference(3);

//        validade = this.addFieldDate("validade");
//        validade
//                .asAtr()
//                .label("Validade (se houver)")
//                .asAtrBootstrap()
//                .colPreference(3);
//        validade.addInstanceValidator(this::isDataValidadeVencida);

        tipoEmbarcacao = this.addFieldString("tipoEmbarcacao");
        tipoEmbarcacao
                .asAtr()
                .required(Resolucao912Form.OBRIGATORIO)
                .label("Embarcação")
                .asAtrBootstrap()
                .colPreference(3);

        tipoEmbarcacao.selectionOfEnum(TipoPropriedadeEmbarcacao.class);

        
        naturezaTransporte = this.addFieldString("naturezaTransporte");
        naturezaTransporte.selectionOf("Misto", "Passageiros");
        naturezaTransporte.asAtr().label("Natureza do transporte");
        naturezaTransporte.asAtrBootstrap().colPreference(3);

        
        habilitacaoTecnicaList = this.addFieldListOf("habilitacaoTecnicaList", STypeHabilitacaoTecnica.class);
        habilitacaoTecnicaList.asAtr().label("Habilitação técnica da embarcação");
        habilitacaoTecnicaList.withInitListener(list -> list.addNew());
        habilitacaoTecnicaList.withMiniumSizeOf(1);
        habilitacaoTecnicaList.withMaximumSizeOf(1);
        habilitacaoTecnicaList.withView(() -> new SViewListByMasterDetail()
                .fullSize()
                .disableInsert()
                .disableDelete()
                .disableNew()
                .col("Habilitação Técnica", "Editar anexos de habilitação técnica"));

        habilitacaoTecnicaList.getElementsType().cascoNuComp
                .asAtr()
                .exists(this::isCascoNu);
        habilitacaoTecnicaList.getElementsType().contrucaoReformaComp
                .asAtr()
                .exists(this::isConstrucao);

        this.asAtr()
                .displayString("Embarcação ${nome!}")
                .dependsOn(nome);

    }

//    private void isDataValidadeVencida(InstanceValidatable<SIDate> validatable) {
//        Date dataValidade = validatable.getInstance().getValue();
//        if (dataValidade != null && dataValidade.before(new Date())){
//            validatable.error("A data de validade não pode estar vencida");
//        }
//    }

    private boolean isCascoNu(SInstance habilitacaoTecnica) {
        return isTipoEmbarcacao(habilitacaoTecnica, TipoPropriedadeEmbarcacao.AFREATAMENTO_CASCO_NU);
    }

    private boolean isConstrucao(SInstance habilitacaoTecnica) {
        return isTipoEmbarcacao(habilitacaoTecnica, TipoPropriedadeEmbarcacao.EM_CONSTRUCAO);
    }

    private boolean isTipoEmbarcacao(SInstance habilitacaoTecnica, TipoPropriedadeEmbarcacao tipo) {
        Optional<String> value = habilitacaoTecnica.findNearestValue(tipoEmbarcacao);
        if (value.isPresent()) {
            return tipo.equals(TipoPropriedadeEmbarcacao.valueOf(value.get()));
        }
        return false;
    }

}
