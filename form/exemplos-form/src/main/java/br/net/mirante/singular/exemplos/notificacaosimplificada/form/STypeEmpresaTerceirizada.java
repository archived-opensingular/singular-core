package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada.dominioService;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EtapaFabricacao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.corporativo.PessoaJuridicaNS;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.core.STypeString;

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
                .displayString("${razaoSocial} - ${endereco}")
                .getTipo().withView(SViewAutoComplete::new);

        empresa.withSelectionFromProvider(razaoSocial, (ins, filter) -> {
            final SIList<?> list = ins.getType().newList();
            for (PessoaJuridicaNS pj : dominioService(ins).empresaTerceirizada(filter)) {
                final SIComposite c = (SIComposite) list.addNew();
                c.setValue(idEmpresa, pj.getCod());
                c.setValue(razaoSocial, pj.getRazaoSocial());
                c.setValue(endereco, pj.getEnderecoCompleto());
            }
            return list;
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
