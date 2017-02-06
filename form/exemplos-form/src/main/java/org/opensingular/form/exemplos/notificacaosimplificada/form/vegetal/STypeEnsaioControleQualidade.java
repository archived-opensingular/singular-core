/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.exemplos.notificacaosimplificada.form.vegetal;

import org.opensingular.form.*;
import org.opensingular.form.converter.SInstanceConverter;
import org.opensingular.form.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import org.opensingular.form.exemplos.notificacaosimplificada.form.STypeFarmacopeiaReferencia;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewSelectionByRadio;
import org.opensingular.form.view.SViewTextArea;

import java.util.Arrays;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEnsaioControleQualidade extends STypeComposite<SIComposite> {

    public STypeString  descricaoTipoEnsaio;
    public STypeString  descricaoTipoReferencia;
    public STypeInteger idTipoEnsaio;
    public STypeComposite<SIComposite> tipoEnsaio;
    public STypeComposite<SIComposite> tipoReferencia;
    public STypeComposite<SIComposite> informacoesFarmacopeicas;
    public STypeAttachmentList resultadosLote;
    public STypeString         justificativa;
    public STypeAttachmentList resultadosControleQualidade;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        tipoEnsaio = this.addFieldComposite("tipoEnsaio");
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

        tipoReferencia = this.addFieldComposite("tipoReferencia");
        STypeInteger idTipoReferencia = tipoReferencia.addFieldInteger("id");
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
           informacoesFarmacopeicas = this.addFieldComposite("informacoesFarmacopeicas");
           informacoesFarmacopeicas
                    .asAtr()
                    .dependsOn(tipoReferencia)
                    .visible(i -> TipoReferencia.FARMACOPEICO.getId().equals(Value.of(i, idTipoReferencia)));

            informacoesFarmacopeicas.addField("farmacopeia", STypeFarmacopeiaReferencia.class);

        }

        {
            resultadosLote = this.addFieldListOfAttachment("resultadosLote", "resuladoLote");
            resultadosLote.asAtr().label("Metodologia/Especificação/Resultado para um lote")
                    .dependsOn(tipoReferencia)
                    .visible(i -> TipoReferencia.NAO_FARMACOPEICO.getId().equals(Value.of(i, idTipoReferencia)));

            STypeAttachment f           = resultadosLote.getElementsType();
            SType<?> nomeArquivo = f.getField(STypeAttachment.FIELD_NAME);
            nomeArquivo.asAtr().label("Nome do Arquivo");
        }

        justificativa = this.addFieldString("justificativa");
        justificativa
                .asAtr().dependsOn(tipoReferencia)
                .label("Justificativa").visible(i -> TipoReferencia.NAO_SE_APLICA.getId().equals(Value.of(i, idTipoReferencia)))
                .maxLength(600)
                .getTipo().withView(SViewTextArea::new);

        resultadosControleQualidade = this.addFieldListOfAttachment("resultadosControleQualidade", "resultado");
        resultadosControleQualidade
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
