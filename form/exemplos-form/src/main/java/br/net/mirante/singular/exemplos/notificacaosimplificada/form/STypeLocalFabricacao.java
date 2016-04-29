package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.corporativo.PessoaJuridicaNS;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.gas.SPackageNotificacaoSimplificadaGasMedicinal;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionByRadio;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.FilteredProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import java.util.Arrays;

import static br.net.mirante.singular.form.mform.util.SQuery.$;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeLocalFabricacao extends STypeComposite<SIComposite> {

    public  STypeSimple                 tipoLocalFabricacao;
    public  STypeEmpresaPropria         empresaPropria;
    public  STypeEmpresaTerceirizada    empresaTerceirizada;
    public  STypeComposite<SIComposite> outroLocalFabricacao;
    private STypeComposite<SIComposite> envasadora;

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this.asAtrBasic().label("Local de Fabricação");

        tipoLocalFabricacao = this.addFieldInteger("tipoLocalFabricacao");
        tipoLocalFabricacao
                .asAtrBasic()
                .label("Tipo de local");

        tipoLocalFabricacao
                .selectionOf(LocalFabricacao.class, new SViewSelectionByRadio())
                .id((i) -> ((LocalFabricacao) i).getId().toString())
                .display((i) -> ((LocalFabricacao) i).getDescricao())
                .converter(new SInstanceConverter<LocalFabricacao, SInstance>() {
                    @Override
                    public void fillInstance(SInstance ins, LocalFabricacao obj) {
                        ins.setValue(obj.getId());
                    }

                    @Override
                    public LocalFabricacao toObject(SInstance ins) {
                        return Arrays.asList(LocalFabricacao.values()).stream().filter(l -> l.getId().equals(ins.getValue())).findFirst().orElse(null);
                    }
                })
                .simpleProviderOf(LocalFabricacao.values());

        empresaPropria = this.addField("empresaPropria", STypeEmpresaPropria.class);

        empresaPropria.withUpdateListener((i) ->
                $(i)
                        .find(empresaPropria.razaoSocialPropria).val("Empresa de teste").end()
                        .find(empresaPropria.cnpj).val("11111111000191").end()
                        .find(empresaPropria.endereco).val("SCLN 211 BLOCO B SUBSOLO").end());

        empresaPropria.asAtrBasic()
                .dependsOn(tipoLocalFabricacao)
                .visible(i -> LocalFabricacao.PRODUCAO_PROPRIA.getId().equals(Value.of(i, tipoLocalFabricacao)));

        final STypeEmpresaInternacional empresaInternacional = this.addField("empresaInternacional", STypeEmpresaInternacional.class);

        empresaInternacional
                .asAtrBasic()
                .label("Empresa Internacional")
                .dependsOn(tipoLocalFabricacao)
                .visible(i -> LocalFabricacao.EMPRESA_INTERNACIONAL.getId().equals(Value.of(i, tipoLocalFabricacao)));

        empresaTerceirizada = this.addField("empresaTerceirizada", STypeEmpresaTerceirizada.class);

        empresaTerceirizada
                .asAtrBasic()
                .dependsOn(tipoLocalFabricacao)
                .visible(i -> LocalFabricacao.EMPRESA_TERCEIRIZADA.getId().equals(Value.of(i, tipoLocalFabricacao)));


        outroLocalFabricacao = this.addFieldComposite("outroLocalFabricacao");

        STypeString idOutroLocalFabricacao          = outroLocalFabricacao.addFieldString("id");
        STypeString razaoSocialOutroLocalFabricacao = outroLocalFabricacao.addFieldString("razaoSocial");
        razaoSocialOutroLocalFabricacao.asAtrBasic().label("Razão Social");
        STypeString enderecoOutroLocalFabricacao = outroLocalFabricacao.addFieldString("endereco");
        outroLocalFabricacao
                .asAtrBasic().label("Outro local de fabricação")
                .dependsOn(tipoLocalFabricacao)
                .visible(i -> LocalFabricacao.OUTRO_LOCAL_FABRICACAO.getId().equals(Value.of(i, tipoLocalFabricacao)));

        outroLocalFabricacao.autocompleteOf(PessoaJuridicaNS.class)
                .id(PessoaJuridicaNS::getCod)
                .display(PessoaJuridicaNS::getRazaoSocial)
                .converter(new PessoaJuridicaConverter(idOutroLocalFabricacao, razaoSocialOutroLocalFabricacao, enderecoOutroLocalFabricacao))
                .filteredProvider((FilteredProvider<PessoaJuridicaNS, SIComposite>) (ins, query) -> dominioService(ins).outroLocalFabricacao(query));


        envasadora = this.addFieldComposite("envasadora");

        STypeString idEnvasadora          = envasadora.addFieldString("id");
        STypeString razaoSocialEnvasadora = envasadora.addFieldString("razaoSocial");
        razaoSocialEnvasadora.asAtrBasic().label("Razão Social");
        STypeString enderecoEnvasadora = envasadora.addFieldString("endereco");
        envasadora
                .asAtrBasic().label("Envasadora")
                .dependsOn(tipoLocalFabricacao)
                .visible(i -> LocalFabricacao.ENVASADORA.getId().equals(Value.of(i, tipoLocalFabricacao)));

        envasadora
                .autocompleteOf(PessoaJuridicaNS.class)
                .id(PessoaJuridicaNS::getCod)
                .display(PessoaJuridicaNS::getRazaoSocial)
                .converter(new PessoaJuridicaConverter(idEnvasadora, razaoSocialEnvasadora, enderecoEnvasadora))
                .filteredProvider((FilteredProvider<PessoaJuridicaNS, SIComposite>) (ins, query) -> dominioService(ins).outroLocalFabricacao(query));

    }

    private boolean isGas(SInstance ins) {
        return getRoot(ins).getType().getNameSimple().equalsIgnoreCase(SPackageNotificacaoSimplificadaGasMedicinal.TIPO);
    }

    private SInstance getRoot(SInstance instance) {
        while (instance.getParent() != null) {
            instance = instance.getParent();
        }
        return instance;
    }

    enum LocalFabricacao {
        PRODUCAO_PROPRIA(1, "Produção Própria"),
        EMPRESA_INTERNACIONAL(2, "Empresa Internacional"),
        EMPRESA_TERCEIRIZADA(3, "Empresa Terceirizada"),
        OUTRO_LOCAL_FABRICACAO(4, "Outro Local de Fabricação"),
        ENVASADORA(5, "Envasadora");

        private Integer id;
        private String  descricao;

        LocalFabricacao(Integer id, String descricao) {
            this.id = id;
            this.descricao = descricao;
        }

        public Integer getId() {
            return id;
        }

        public String getDescricao() {
            return descricao;
        }

        public static LocalFabricacao[] getValues(boolean gas) {
            if (gas) {
                return values();
            } else {
                return new LocalFabricacao[]{PRODUCAO_PROPRIA, EMPRESA_INTERNACIONAL,
                        EMPRESA_TERCEIRIZADA, OUTRO_LOCAL_FABRICACAO};
            }
        }
    }

    public static class PessoaJuridicaConverter implements SInstanceConverter<PessoaJuridicaNS, SIComposite> {

        private final String idOutroLocalFabricacao;
        private final String razaoSocialOutroLocalFabricacao;
        private final String enderecoOutroLocalFabricacao;

        public PessoaJuridicaConverter(SType idOutroLocalFabricacao, SType razaoSocialOutroLocalFabricacao, SType enderecoOutroLocalFabricacao) {
            this.idOutroLocalFabricacao = idOutroLocalFabricacao.getNameSimple();
            this.razaoSocialOutroLocalFabricacao = razaoSocialOutroLocalFabricacao.getNameSimple();
            this.enderecoOutroLocalFabricacao = enderecoOutroLocalFabricacao.getNameSimple();
        }

        @Override
        public void fillInstance(SIComposite ins, PessoaJuridicaNS obj) {
            ins.setValue(idOutroLocalFabricacao, obj.getCod());
            ins.setValue(razaoSocialOutroLocalFabricacao, obj.getRazaoSocial());
            ins.setValue(enderecoOutroLocalFabricacao, obj.getEnderecoCompleto());
        }

        @Override
        public PessoaJuridicaNS toObject(SIComposite ins) {
            return dominioService(ins)
                    .buscarLocalFabricacao(Value.of(ins, idOutroLocalFabricacao));
        }

    }

}
