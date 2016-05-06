package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.EtapaFabricacao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.converter.VocabularioControladoDTOSInstanceConverter;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.corporativo.PessoaJuridicaNS;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.dto.VocabularioControladoDTO;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.provider.VocabularioControladoTextQueryProvider;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewAutoComplete;
import br.net.mirante.singular.form.view.SViewListByTable;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeLocalFabricacao.dominioService;

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
                .filteredProvider((i,f) -> dominioService(i).empresaTerceirizada(f));
        
        STypeList<STypeComposite<SIComposite>, SIComposite> etapasFabricacao       = addFieldListOfComposite("etapasFabricacao", "etapaFabricacaoWrapper");
        STypeComposite<SIComposite>                         etapaFabricacaoWrapper = etapasFabricacao.getElementsType();
        STypeComposite<SIComposite>                         etapaFabricacao        = etapaFabricacaoWrapper.addFieldComposite("etapaFabricacao");
        STypeString                                         idEtapaFabricacao      = etapaFabricacao.addFieldString("id");
        STypeString                                         descricaoEtapaFabricacao = etapaFabricacao.addFieldString("descricao");

        etapaFabricacao
                .setView(SViewAutoComplete::new);

        etapaFabricacao.autocompleteOf(VocabularioControladoDTO.class)
                .id(VocabularioControladoDTO::getId)
                .display(VocabularioControladoDTO::getDescricao)
                .converter(new VocabularioControladoDTOSInstanceConverter(idEtapaFabricacao, descricaoEtapaFabricacao))
                .filteredProvider(new VocabularioControladoTextQueryProvider<>(EtapaFabricacao.class));

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
