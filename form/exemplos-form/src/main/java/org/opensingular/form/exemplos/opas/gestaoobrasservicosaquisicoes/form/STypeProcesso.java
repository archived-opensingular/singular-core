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
package org.opensingular.form.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import org.opensingular.form.exemplos.emec.credenciamentoescolagoverno.form.STypeMunicipio;
import org.opensingular.form.exemplos.emec.credenciamentoescolagoverno.form.STypeEstado;
import org.opensingular.form.exemplos.opas.gestaoobrasservicosaquisicoes.enums.AcaoGestaoObras;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.internal.xml.ConversorToolkit;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewListByMasterDetail;

@SInfoType(spackage = SPackageGestaoObrasServicosAquisicoes.class)
public class STypeProcesso extends STypeComposite<SIComposite> {

    private static final String FIELD_OBRAS_PROCESSO = "obrasProcesso";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        addDadosProcesso();
        addObrasProcesso();
        
        setView(SViewByBlock::new)
            .newBlock("Dados do Processo").add("dadosProcesso")
            .newBlock("Obras do Processo").add(FIELD_OBRAS_PROCESSO);
    }

    private void addDadosProcesso(){
        final STypeComposite<SIComposite> dadosProcesso = this.addFieldComposite("dadosProcesso");
        
        final STypeString acao = dadosProcesso.addFieldString("acao", true);
        acao.selectionOfEnum(AcaoGestaoObras.class)
            .asAtr().label("Ação").asAtrBootstrap().colPreference(4);

        final STypeString tipologia = dadosProcesso.addFieldString("tipologia", true);
        tipologia.asAtr().label("Tipologia").dependsOn(acao)
            .asAtrBootstrap().colPreference(4);
        tipologia
            .selection()
            .selfIdAndDisplay()
            .simpleProvider(builder -> {
                AcaoGestaoObras acaoGestaoObras = builder.findNearest(acao).get().getValue(AcaoGestaoObras.class);
                return Optional.ofNullable(acaoGestaoObras).map(AcaoGestaoObras::getTipologias).orElse(Collections.emptyList());
            });
    
        final STypeString tipo = dadosProcesso.addFieldString("tipo", true);
        tipo.selectionOf("Implementação", "Reforma", "Ampliação", "Reforma/Ampliação")
            .asAtr().label("Tipo")
            .asAtrBootstrap().colPreference(4);
        
        final STypeString numSIPAR = dadosProcesso.addFieldString("numSIPAR", true);
        numSIPAR.asAtr().label("Nº SIPAR").asAtrBootstrap().colPreference(4);

        final STypeEstado uf = dadosProcesso.addField("uf", STypeEstado.class, true);
        uf.asAtr().label("UF").asAtrBootstrap().colPreference(4);
        
        final STypeString dsei = dadosProcesso.addFieldString("dsei", true);
        dsei.selectionOf("DSEI 1", "DSEI 2").withSelectView()
            .asAtr().label("DSEI")
            .asAtrBootstrap().colPreference(4);
        
        final STypeMunicipio municipio = dadosProcesso.addField("municipio", STypeMunicipio.class, true);
        municipio.selectionByUF(uf)
            .asAtr().label("Município").asAtrBootstrap().colPreference(4);

        final STypeString executor = dadosProcesso.addFieldString("executor", true);
        executor.selectionOf("SESAI", "Min. da Integração", "FUNASA", "Outros")
            .asAtr().label("Executor")
            .asAtrBootstrap().colPreference(4);

        final STypeString fonteRecurso = dadosProcesso.addFieldString("fonteRecurso", true);
        fonteRecurso.selectionOf("Estruturação", "AC. DE COOPERAÇÃO TÉCNICA").withSelectView()
            .asAtr().label("Fonte de Recurso")
            .asAtrBootstrap().colPreference(4);
        
        final STypeList<STypeAldeia, SIComposite> aldeias = dadosProcesso.addFieldListOf("aldeias", STypeAldeia.class);
        aldeias.getElementsType().mockSelection().asAtr().label("Aldeia");
        aldeias.withView(SViewListByMasterDetail::new)
            .asAtr().itemLabel("Aldeia");
//        
//        final Map<String, Integer> mapaQtdFamilias = new HashMap<>(4);
//        mapaQtdFamilias.put("Baniuas", 1035);
//        mapaQtdFamilias.put("Guaranis", 13789);
//        mapaQtdFamilias.put("Uapixanas", 1273);
//        mapaQtdFamilias.put("Caiapós", 2357);
//        
//        final Map<String, Integer> mapaPopulacao = new HashMap<>(4);
//        mapaPopulacao.put("Baniuas", 5141);
//        mapaPopulacao.put("Guaranis", 34350);
//        mapaPopulacao.put("Uapixanas", 6589);
//        mapaPopulacao.put("Caiapós", 7096);
//        final STypeString aldeia = aldeias.withMiniumSizeOf(1).getElementsType().addFieldString(STypeAldeia.FIELD_NOME, true);
//        aldeia.asAtr().label("Aldeia");
//        aldeia.selection()
//            .selfIdAndDisplay()
//            .simpleProviderOf("Baniuas", "Guaranis", "Uapixanas", "Caiapós");
//        aldeias.getElementsType().addFieldInteger(STypeAldeia.FIELD_QTD_FAMILIAS, true)
//            .withUpdateListener(instance -> {
//                instance.setValue(instance.findNearestValue(aldeia).map(mapaQtdFamilias::get).orElse(null));
//            })
//            .asAtr().dependsOn(aldeia).label("Nº Famílias").enabled(false);
//        aldeias.getElementsType().addFieldInteger(STypeAldeia.FIELD_POPULACAO, true)
//            .withUpdateListener(instance -> {
//                instance.setValue(instance.findNearestValue(aldeia).map(mapaPopulacao::get).orElse(null));
//            })
//            .asAtr().dependsOn(aldeia).label("População").enabled(false);
    }
    
    public void addObrasProcesso(){
        final STypeList<STypeObra, SIComposite> obrasProcesso = this.addFieldListOf(FIELD_OBRAS_PROCESSO, STypeObra.class);
        obrasProcesso.asAtr().itemLabel("Obra");
        obrasProcesso.withView(new SViewListByMasterDetail(),
            view -> view.col(obrasProcesso.getElementsType().getFieldValorSolicitado()), 
            view -> view.col(obrasProcesso.getElementsType().getFieldValorContratado()), 
            view -> view.col("Valor Empenhado", instancia -> {
                final BigDecimal valorEmpenhado = ((SIComposite)instancia).getFieldList(STypeObra.FIELD_VALORES_EMPENHADOS, SIComposite.class)
                    .stream().map(instanciaComposta -> (BigDecimal) Value.of(instanciaComposta, STypeValorEmpenhadoObra.FIELD_VALOR_EMPENHADO))
                    .filter(valor -> valor != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                return ConversorToolkit.printNumber(valorEmpenhado, 2);
            }),
            view -> view.col(getFieldObrasProcesso().getElementsType().getFieldNumContrato()));
    }
    
    @SuppressWarnings("unchecked")
    public STypeList<STypeObra, SIComposite> getFieldObrasProcesso(){
        return (STypeList<STypeObra, SIComposite>) getField(FIELD_OBRAS_PROCESSO);
    }
}
