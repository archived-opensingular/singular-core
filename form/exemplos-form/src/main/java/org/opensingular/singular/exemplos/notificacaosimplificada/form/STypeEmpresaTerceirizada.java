package org.opensingular.singular.exemplos.notificacaosimplificada.form;

import org.opensingular.singular.exemplos.notificacaosimplificada.domain.EtapaFabricacao;
import org.opensingular.singular.exemplos.notificacaosimplificada.domain.corporativo.PessoaJuridicaNS;
import org.opensingular.singular.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.provider.STextQueryProvider;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.view.SViewAutoComplete;
import org.opensingular.singular.form.view.SViewListByTable;

import static org.opensingular.singular.exemplos.notificacaosimplificada.form.STypeLocalFabricacao.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEmpresaTerceirizada extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {

        STypeComposite<SIComposite> empresa     = addFieldComposite("empresa");
        STypeString                 idEmpresa   = empresa.addFieldString("id");
        STypeString                 razaoSocial = empresa.addFieldString("razaoSocial");
        razaoSocial.asAtr().label("Razão Social");
        STypeString endereco = empresa.addFieldString("endereco");
        empresa
                .asAtr().label("Empresa")
                .displayString("${razaoSocial} - ${endereco}")

                .getTipo().withView(SViewAutoComplete::new);

        empresa.autocompleteOf(PessoaJuridicaNS.class)
                .id(PessoaJuridicaNS::getCod)
                .display(PessoaJuridicaNS::getRazaoSocial)
                .converter(new STypeLocalFabricacao.PessoaJuridicaConverter(idEmpresa, razaoSocial, endereco))
                .filteredProvider((i, f) -> dominioService(i).empresaTerceirizada(f));

        STypeList<STypeComposite<SIComposite>, SIComposite> etapasFabricacao         = addFieldListOfComposite("etapasFabricacao", "etapaFabricacaoWrapper");
        STypeComposite<SIComposite>                         etapaFabricacaoWrapper   = etapasFabricacao.getElementsType();
        STypeComposite<SIComposite>                         etapaFabricacao          = etapaFabricacaoWrapper.addFieldComposite("etapaFabricacao");
        STypeString                                         idEtapaFabricacao        = etapaFabricacao.addFieldString("id");
        STypeString                                         descricaoEtapaFabricacao = etapaFabricacao.addFieldString("descricao");

        etapaFabricacao
                .setView(SViewAutoComplete::new);

        etapaFabricacao.autocomplete()
                .id(idEtapaFabricacao)
                .display(descricaoEtapaFabricacao)
                .filteredProvider((STextQueryProvider) (builder, query) -> {
                    builder
                            .getCurrentInstance()
                            .getDocument()
                            .lookupService(DominioService.class)
                            .buscarVocabulario(EtapaFabricacao.class, query)
                            .forEach(vc -> builder.add().set(idEtapaFabricacao, vc.getId()).set(descricaoEtapaFabricacao, vc.getDescricao()));
                });

        etapasFabricacao
                .withView(SViewListByTable::new);
        etapasFabricacao
                .asAtr()
                .label("Etapa de fabricação")
                .displayString("<#list _inst as c>${c.etapaFabricacao.descricao}<#sep>, </#sep></#list>");
    }

    public STypeList<STypeComposite<SIComposite>, SIComposite> etapasFabricacao() {
        return (STypeList<STypeComposite<SIComposite>, SIComposite>) getField("etapasFabricacao");
    }


}
