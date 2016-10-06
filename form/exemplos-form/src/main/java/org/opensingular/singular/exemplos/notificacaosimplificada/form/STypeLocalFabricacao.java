package org.opensingular.singular.exemplos.notificacaosimplificada.form;

import org.opensingular.singular.exemplos.notificacaosimplificada.domain.corporativo.PessoaJuridicaNS;
import org.opensingular.singular.exemplos.notificacaosimplificada.form.gas.STypeNotificacaoSimplificadaGasMedicinal;
import org.opensingular.singular.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.SType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.converter.SInstanceConverter;
import org.opensingular.singular.form.provider.TextQueryProvider;
import org.opensingular.singular.form.type.core.SIInteger;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.util.transformer.Value;
import org.opensingular.singular.form.view.SViewSelectionByRadio;

import java.util.Arrays;


@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeLocalFabricacao extends STypeComposite<SIComposite> {

    public STypeInteger tipoLocalFabricacao;
    public STypeEmpresaPropria         empresaPropria;
    public STypeEmpresaTerceirizada    empresaTerceirizada;
    public STypeComposite<SIComposite> outroLocalFabricacao;
    public STypeEmpresaInternacional empresaInternacional;
    public STypeComposite<SIComposite> envasadora;

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this.asAtr().label("Local de Fabricação");

        tipoLocalFabricacao = this.addFieldInteger("tipoLocalFabricacao");
        tipoLocalFabricacao
                .asAtr()
                .label("Tipo de local");

        tipoLocalFabricacao
                .selectionOf(LocalFabricacao.class, new SViewSelectionByRadio())
                .id((i) -> ((LocalFabricacao) i).getId().toString())
                .display((i) -> ((LocalFabricacao) i).getDescricao())
                .converter(new SInstanceConverter<LocalFabricacao, SIInteger>() {
                    @Override
                    public void fillInstance(SIInteger ins, LocalFabricacao obj) {
                        ins.setValue(obj.getId());
                    }

                    @Override
                    public LocalFabricacao toObject(SIInteger ins) {
                        return Arrays.asList(LocalFabricacao.values()).stream().filter(l -> l.getId().equals(
                                ins.getValue())).findFirst().orElse(null);
                    }
                })
                .simpleProviderOf(LocalFabricacao.values());

        empresaPropria = this.addField("empresaPropria", STypeEmpresaPropria.class);

        empresaPropria.withUpdateListener((ins) -> {
                    ins.findNearest(empresaPropria.razaoSocial).ifPresent(ins2 -> ins2.setValue("Empresa de teste"));
                    ins.findNearest(empresaPropria.cnpj).ifPresent(ins2 -> ins2.setValue("11111111000191"));
                    ins.findNearest(empresaPropria.endereco).ifPresent(ins2 -> ins2.setValue("SCLN 211 BLOCO B SUBSOLO"));
                }
        );

        empresaPropria.asAtr()
                .dependsOn(tipoLocalFabricacao)
                .visible(i -> LocalFabricacao.PRODUCAO_PROPRIA.getId().equals(Value.of(i, tipoLocalFabricacao)));

        empresaInternacional = this.addField("empresaInternacional", STypeEmpresaInternacional.class);

        empresaInternacional
                .asAtr()
                .label("Empresa Internacional")
                .dependsOn(tipoLocalFabricacao)
                .visible(i -> LocalFabricacao.EMPRESA_INTERNACIONAL.getId().equals(Value.of(i, tipoLocalFabricacao)));

        empresaTerceirizada = this.addField("empresaTerceirizada", STypeEmpresaTerceirizada.class);

        empresaTerceirizada
                .asAtr()
                .dependsOn(tipoLocalFabricacao)
                .visible(i -> LocalFabricacao.EMPRESA_TERCEIRIZADA.getId().equals(Value.of(i, tipoLocalFabricacao)));


        outroLocalFabricacao = this.addFieldComposite("outroLocalFabricacao");

        STypeString idOutroLocalFabricacao          = outroLocalFabricacao.addFieldString("id");
        STypeString razaoSocialOutroLocalFabricacao = outroLocalFabricacao.addFieldString("razaoSocial");
        razaoSocialOutroLocalFabricacao.asAtr().label("Razão Social");
        STypeString enderecoOutroLocalFabricacao = outroLocalFabricacao.addFieldString("endereco");
        outroLocalFabricacao
                .asAtr().label("Outro local de fabricação")
                .dependsOn(tipoLocalFabricacao)
                .visible(i -> LocalFabricacao.OUTRO_LOCAL_FABRICACAO.getId().equals(Value.of(i, tipoLocalFabricacao)));

        outroLocalFabricacao.autocompleteOf(PessoaJuridicaNS.class)
                .id(PessoaJuridicaNS::getCod)
                .display(PessoaJuridicaNS::getRazaoSocial)
                .converter(new PessoaJuridicaConverter(idOutroLocalFabricacao, razaoSocialOutroLocalFabricacao, enderecoOutroLocalFabricacao))
                .filteredProvider((TextQueryProvider<PessoaJuridicaNS, SIComposite>) (ins, query) -> dominioService(ins).outroLocalFabricacao(query));


        envasadora = this.addFieldComposite("envasadora");

        STypeString idEnvasadora          = envasadora.addFieldString("id");
        STypeString razaoSocialEnvasadora = envasadora.addFieldString("razaoSocial");
        razaoSocialEnvasadora.asAtr().label("Razão Social");
        STypeString enderecoEnvasadora = envasadora.addFieldString("endereco");
        envasadora
                .asAtr().label("Envasadora")
                .dependsOn(tipoLocalFabricacao)
                .visible(i -> LocalFabricacao.ENVASADORA.getId().equals(Value.of(i, tipoLocalFabricacao)));

        envasadora
                .autocompleteOf(PessoaJuridicaNS.class)
                .id(PessoaJuridicaNS::getCod)
                .display(PessoaJuridicaNS::getRazaoSocial)
                .converter(new PessoaJuridicaConverter(idEnvasadora, razaoSocialEnvasadora, enderecoEnvasadora))
                .filteredProvider((TextQueryProvider<PessoaJuridicaNS, SIComposite>) (ins, query) -> dominioService(ins).outroLocalFabricacao(query));

    }

    private boolean isGas(SInstance ins) {
        return getRoot(ins).getType().getClass().equals(STypeNotificacaoSimplificadaGasMedicinal.class);
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
