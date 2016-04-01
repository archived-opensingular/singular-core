package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EtapaFabricacao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco.SPackageNotificacaoSimplificadaBaixoRisco;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.core.STypeString;
import org.apache.commons.lang3.tuple.Triple;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEmpresaTerceirizada extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {

        STypeComposite<SIComposite> empresa     = addFieldComposite("empresa");
        STypeString                 idEmpresa   = empresa.addFieldString("id");
        STypeString                 razaoSocial = empresa.addFieldString("razaoSocial");
        razaoSocial.asAtrBasic().label("Razão Social");
        STypeString endereco = empresa.addFieldString("endereco");
        empresa
                .asAtrBasic().label("Empresa")
                .getTipo().withView(SViewAutoComplete::new);

        empresa.withSelectionFromProvider(razaoSocial, (optionsInstance, lb) -> {
            for (Triple t : dominioService(optionsInstance).empresaTerceirizada()) {
                lb
                        .add()
                        .set(idEmpresa, t.getLeft())
                        .set(razaoSocial, t.getMiddle())
                        .set(endereco, t.getRight());
            }
        });


        STypeList<STypeComposite<SIComposite>, SIComposite> etapasFabricacao         = addFieldListOfComposite("etapasFabricacao", "etapaFabricacaoWrapper");
        STypeComposite<SIComposite>                         etapaFabricacaoWrapper   = etapasFabricacao.getElementsType();
        STypeComposite<SIComposite>                         etapaFabricacao          = etapaFabricacaoWrapper.addFieldComposite("etapaFabricacao");
        STypeString                                         idEtapaFabricacao        = etapaFabricacao.addFieldString("id");
        STypeString                                         descricaoEtapaFabricacao = etapaFabricacao.addFieldString("descricao");

        etapaFabricacao
                .setView(SViewAutoComplete::new);

        etapaFabricacao.withSelectionFromProvider(descricaoEtapaFabricacao, (ins, filter) -> {
            final SIList<?> list = ins.getType().newList();
            for (EtapaFabricacao ef : dominioService(ins).etapaFabricacao(filter)) {
                final SIComposite c = (SIComposite) list.addNew();
                c.setValue(idEtapaFabricacao, ef.getId());
                c.setValue(descricaoEtapaFabricacao, ef.getDescricao());
            }
            return list;
        });

        etapasFabricacao
                .withView(SViewListByTable::new);
        etapasFabricacao
                .asAtrBasic()
                .label("Etapa de fabricação")
                .displayString("<#list _inst as c>${c.etapaFabricacao.descricao}<#sep>, </#sep></#list>");
    }

    public STypeList<STypeComposite<SIComposite>, SIComposite> etapasFabricacao() {
        return (STypeList<STypeComposite<SIComposite>, SIComposite>) getField("etapasFabricacao");
    }


}
