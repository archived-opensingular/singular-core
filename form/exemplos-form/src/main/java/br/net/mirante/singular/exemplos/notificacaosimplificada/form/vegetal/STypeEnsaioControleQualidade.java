/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vegetal;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeFarmacopeiaReferencia;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionByRadio;
import br.net.mirante.singular.form.mform.basic.view.SViewTextArea;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import java.util.Arrays;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEnsaioControleQualidade extends STypeComposite<SIComposite> {

    public STypeString  descricaoTipoEnsaio;
    public STypeString  descricaoTipoReferencia;
    public STypeInteger idTipoEnsaio;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        STypeComposite<SIComposite> tipoEnsaio = this.addFieldComposite("tipoEnsaio");
        idTipoEnsaio = tipoEnsaio.addFieldInteger("id");
        descricaoTipoEnsaio = tipoEnsaio.addFieldString("descricao");
        tipoEnsaio.selectionOf(TipoEnsaioControleQualidade.class)
                .id(TipoEnsaioControleQualidade::getId)
                .display(TipoEnsaioControleQualidade::getDescricao)
                .converter(new SInstanceConverter<TipoEnsaioControleQualidade, SIComposite>() {
                    @Override
                    public void fillInstance(SIComposite ins, TipoEnsaioControleQualidade obj) {
                        ins.setValue(idTipoEnsaio, obj.getId());
                        ins.setValue(descricaoTipoEnsaio, obj.getDescricao());
                    }

                    @Override
                    public TipoEnsaioControleQualidade toObject(SIComposite ins) {
                        return Arrays.asList(TipoEnsaioControleQualidade.values())
                                .stream()
                                .filter(t -> t.getId().equals(Value.of(ins, idTipoEnsaio)))
                                .findFirst()
                                .orElse(null);
                    }
                }).simpleProviderOf(TipoEnsaioControleQualidade.values());

        tipoEnsaio.asAtr().label("Ensaio de Controle de Qualidade").enabled(false);

        STypeComposite<SIComposite> tipoReferencia   = this.addFieldComposite("tipoReferencia");
        STypeInteger                idTipoReferencia = tipoReferencia.addFieldInteger("id");
        descricaoTipoReferencia = tipoReferencia.addFieldString("descricao");

        tipoReferencia.selectionOf(TipoReferencia.class, new SViewSelectionByRadio())
                .id(TipoReferencia::getId)
                .display(TipoReferencia::getDescricao)
                .converter(new SInstanceConverter<TipoReferencia, SIComposite>() {
                    @Override
                    public void fillInstance(SIComposite ins, TipoReferencia obj) {
                        ins.setValue(idTipoReferencia, obj.getId());
                        ins.setValue(descricaoTipoReferencia, obj.getDescricao());
                    }

                    @Override
                    public TipoReferencia toObject(SIComposite ins) {
                        return Arrays.stream(TipoReferencia.values()).filter(tr -> tr.getId().equals(Value.of(ins, idTipoReferencia))).findFirst().orElse(null);
                    }
                })
                .simpleProviderOf(TipoReferencia.values());

        {
            STypeComposite<SIComposite> informacoesFarmacopeicas = this.addFieldComposite("informacoesFarmacopeicas");
            informacoesFarmacopeicas
                    .asAtr()
                    .dependsOn(tipoReferencia)
                    .visible(i -> TipoReferencia.FARMACOPEICO.getId().equals(Value.of(i, idTipoReferencia)));

            STypeFarmacopeiaReferencia farmacopeia = informacoesFarmacopeicas.addField("farmacopeia", STypeFarmacopeiaReferencia.class);

        }

        {
            STypeAttachmentList resultadosLote = this.addFieldListOfAttachment("resultadosLote", "resuladoLote");
            resultadosLote.asAtr().label("Metodologia/Especificação/Resultado para um lote")
                    .dependsOn(tipoReferencia)
                    .visible(i -> TipoReferencia.NAO_FARMACOPEICO.getId().equals(Value.of(i, idTipoReferencia)));

            STypeAttachment f           = resultadosLote.getElementsType();
            SType<?>        nomeArquivo = (STypeSimple) f.getField(STypeAttachment.FIELD_NAME);
            nomeArquivo.asAtr().label("Nome do Arquivo");
        }

        STypeString justificativa = this.addFieldString("justificativa");
        justificativa
                .asAtr().dependsOn(tipoReferencia)
                .label("Justificativa").visible(i -> TipoReferencia.NAO_SE_APLICA.getId().equals(Value.of(i, idTipoReferencia)))
                .tamanhoMaximo(600)
                .getTipo().withView(SViewTextArea::new);

        this.addFieldListOfAttachment("resultadosControleQualidade", "resultado")
                .asAtr()
                .label("Resultados do controle da qualidade")
                .visible(i -> TipoEnsaioControleQualidade.RESULTADOS.getId().equals(Value.of(i, idTipoEnsaio)))
                .required();

    }

    enum TipoEnsaioControleQualidade {
        PROSPECCAO_FITOQUIMICA_CCD(1, "Perfil cromatográfico"),
        LAUDO_BOTANICO(2, "Teor"),
        RESULTADOS(3, "Resultados do controle da qualidade");

        private Integer id;
        private String  descricao;

        TipoEnsaioControleQualidade(Integer id, String descricao) {
            this.id = id;
            this.descricao = descricao;
        }

        public Integer getId() {
            return id;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    enum TipoReferencia {
        FARMACOPEICO(1, "Farmacopêico"),
        NAO_FARMACOPEICO(2, "Não farmacopêico"),
        NAO_SE_APLICA(3, "Não se aplica");

        private Integer id;
        private String  descricao;

        TipoReferencia(Integer id, String descricao) {
            this.id = id;
            this.descricao = descricao;
        }

        public Integer getId() {
            return id;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
