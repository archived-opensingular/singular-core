package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.SingularPredicates;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;

import java.util.Optional;
import java.util.function.Function;

import static br.net.mirante.singular.exemplos.ggtox.primariasimplificada.TipoPeticaoPrimariaGGTOX.BIOLOGICO;
import static br.net.mirante.singular.exemplos.ggtox.primariasimplificada.TipoPeticaoPrimariaGGTOX.PRESERVATIVO_MADEIRA;
import static br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon.ppsService;
import static br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.STypePeticaoPrimariaSimplificada.OBRIGATORIO;
import static br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.STypePeticaoPrimariaSimplificada.QUANTIDADE_MINIMA;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeProdutoFormuladoPeticaoPrimariaSimplificada extends STypePersistentComposite {


    public STypeFormuladorConformeMatriz           formulador;
    public STypeList<STypeFormulador, SIComposite> formuladores;
    public STypeComposite<SIComposite>             tipoFormulacao;
    public STypeString                             outraSiglaTipoFormulacao;
    public STypeString                             outroNomeTipoFormulacao;


    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        this
                .asAtrAnnotation()
                .setAnnotated();

        this
                .asAtr()
                .label("Produto Formulado");

        tipoFormulacao = addFieldComposite("tipoFormulacao");

        STypeInteger idTipoFormulacao        = tipoFormulacao.addFieldInteger("idTipoFormulacao");
        STypeString  siglaTipoFormulacao     = tipoFormulacao.addFieldString("siglaTipoFormulacao");
        STypeString  nomeTipoFormulacao      = tipoFormulacao.addFieldString("nomeTipoFormulacao");
        STypeString  descricaoTipoFormulacao = tipoFormulacao.addFieldString("descricaoTipoFormulacao");

        outraSiglaTipoFormulacao = addFieldString("outraSiglaTipoFormulacao");
        outroNomeTipoFormulacao = addFieldString("outroNomeTipoFormulacao");

        tipoFormulacao
                .asAtr()
                .required(ins -> OBRIGATORIO && Optional
                        .ofNullable(SInstances.getRootInstance(ins))
                        .map(x -> x.getField("tipoPeticao"))
                        .map(safeCast(SIComposite.class))
                        .map(x -> x.getField("id"))
                        .map(SInstance::getValue)
                        .map(safeCast(Integer.class))
                        .map(id -> !id.equals(PRESERVATIVO_MADEIRA.getId()))
                        .orElse(true))
                .label("Tipo de Formulação")
                .asAtrBootstrap()
                .colPreference(4);

        tipoFormulacao
                .selection()
                .id(idTipoFormulacao)
                .display("<#if siglaTipoFormulacao != ''>${siglaTipoFormulacao} - </#if>${nomeTipoFormulacao}")
                .simpleProvider(sb -> {
                    final Optional<SInstance> optionalOfRoot = Optional.of(SInstances.getRootInstance(sb.getCurrentInstance()));
                    optionalOfRoot
                            .ifPresent(ins -> ppsService(ins)
                                    .buscarTipoDeFormulacao()
                                    .forEach(tipoFormulacaoEntity -> sb.add()
                                            .set(idTipoFormulacao, tipoFormulacaoEntity.getCod())
                                            .set(siglaTipoFormulacao, tipoFormulacaoEntity.getSigla())
                                            .set(nomeTipoFormulacao, tipoFormulacaoEntity.getNome())
                                            .set(descricaoTipoFormulacao, tipoFormulacaoEntity.getDescricao())
                                    )
                            );
                    optionalOfRoot
                            .map(safeCast(SIComposite.class))
                            .map(ins -> ins.getField("tipoPeticao"))
                            .map(safeCast(SIComposite.class))
                            .map(ins -> ins.getField("id"))
                            .map(SInstance::getValue)
                            .map(safeCast(Integer.class))
                            .filter(id -> id.equals(BIOLOGICO.getId()) || id.equals(PRESERVATIVO_MADEIRA.getId()))
                            .ifPresent(id -> sb.add()
                                    .set(idTipoFormulacao, 0)
                                    .set(siglaTipoFormulacao, null)
                                    .set(nomeTipoFormulacao, "Outra")
                                    .set(descricaoTipoFormulacao, null)
                            );
                });

        outraSiglaTipoFormulacao
                .asAtr()
                .required(OBRIGATORIO)
                .dependsOn(tipoFormulacao)
                .visible(SingularPredicates.typeValueIsEqualsTo(nomeTipoFormulacao, "Outra"))
                .label("Sigla");

        outraSiglaTipoFormulacao
                .asAtrBootstrap()
                .colPreference(1);

        outroNomeTipoFormulacao
                .asAtr()
                .required(OBRIGATORIO)
                .dependsOn(tipoFormulacao)
                .visible(SingularPredicates.typeValueIsEqualsTo(nomeTipoFormulacao, "Outra"))
                .label("Nome");

        outroNomeTipoFormulacao
                .asAtrBootstrap()
                .colPreference(3);

        formuladores = addFieldListOf("formuladores", STypeFormulador.class);

        formuladores
                .asAtr()
                .label("Formuladores");
        formuladores
                .withView(new SViewListByMasterDetail()
                        .col(formuladores.getElementsType().tipoEntidade)
                        .col(formuladores.getElementsType().nome)
                        .col(formuladores.getElementsType().enderecoEletronico)
                        .col(formuladores.getElementsType().telefone)
                );
        formuladores
                .withMiniumSizeOf(QUANTIDADE_MINIMA);


        formulador = addField("formulador", STypeFormuladorConformeMatriz.class);
        formulador
                .asAtrBootstrap()
                .colPreference(12)
                .newRow();

    }

    private <T> Function<Object, T> safeCast(Class<T> clasz) {
        return x -> clasz.isAssignableFrom(x.getClass()) ? clasz.cast(x) : null;
    }

}
