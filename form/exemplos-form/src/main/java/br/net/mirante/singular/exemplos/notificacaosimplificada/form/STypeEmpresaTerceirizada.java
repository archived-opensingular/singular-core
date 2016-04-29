package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EtapaFabricacao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.converter.VocabularioControladoDTOSInstanceConverter;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.corporativo.PessoaJuridicaNS;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.dto.VocabularioControladoDTO;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.provider.VocabularioControladoFilteredProvider;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.core.STypeString;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeLocalFabricacao.dominioService;

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

        empresa.autocompleteOf(PessoaJuridicaNS.class)
                .id(PessoaJuridicaNS::getCod)
                .display(PessoaJuridicaNS::getRazaoSocial)
                .converter(new STypeLocalFabricacao.PessoaJuridicaConverter(idEmpresa, razaoSocial, endereco))
                .filteredProvider((i,f) -> dominioService(i).empresaTerceirizada(f));
        
        STypeList<STypeComposite<SIComposite>, SIComposite> etapasFabricacao         = addFieldListOfComposite("etapasFabricacao", "etapaFabricacaoWrapper");
        STypeComposite<SIComposite>                         etapaFabricacaoWrapper   = etapasFabricacao.getElementsType();
        STypeComposite<SIComposite>                         etapaFabricacao          = etapaFabricacaoWrapper.addFieldComposite("etapaFabricacao");
        STypeString                                         idEtapaFabricacao        = etapaFabricacao.addFieldString("id");
        STypeString                                         descricaoEtapaFabricacao = etapaFabricacao.addFieldString("descricao");

        etapaFabricacao
                .setView(SViewAutoComplete::new);

        etapaFabricacao.autocompleteOf(VocabularioControladoDTO.class)
                .id(VocabularioControladoDTO::getId)
                .display(VocabularioControladoDTO::getDescricao)
                .converter(new VocabularioControladoDTOSInstanceConverter(idEtapaFabricacao, descricaoEtapaFabricacao))
                .filteredProvider(new VocabularioControladoFilteredProvider<>(EtapaFabricacao.class));

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
