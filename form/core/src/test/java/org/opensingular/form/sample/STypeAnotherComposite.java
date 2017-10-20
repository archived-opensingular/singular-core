/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewListByTable;

@SInfoType(spackage = FormTestPackage.class, newable = false, name = "STypeAnotherComposite")
public class STypeAnotherComposite extends STypeComposite<SIComposite> {

    public STypeComposite<SIComposite> dadosGerais;
    public STypeString                 regiaoHidrografica;
    public STypeComposite              extremos;

    public STypeComposite<SIComposite> extremo1;
    public STypeString                 extremo1municipio;
    public STypeString                 extremo1pais;
    public STypeString                 extremo1uf;
    public STypeString                 extremo1local;


    public STypeComposite<SIComposite> extremo2;
    public STypeString                 extremo2municipio;
    public STypeString                 extremo2pais;
    public STypeString                 extremo2uf;
    public STypeString                 extremo2local;

    public STypeList<STypeString, SIString> rios;
    public STypeInteger                     frequencia;

    public STypeList<STypeAnotherComposisteChildListElement, SIComposite> esquemasOperacionaisVolta;
    public STypeList<STypeAnotherComposisteChildListElement, SIComposite> esquemasOperacionaisIda;


    @Override
    protected void onLoadType(TypeBuilder tb) {


        dadosGerais = addFieldComposite("dadosGerais");
        dadosGerais.asAtrAnnotation().setAnnotated();

        //1 - 
        regiaoHidrografica = dadosGerais.addFieldString("regiaoHidrografica");
        regiaoHidrografica.selection();
        regiaoHidrografica.withSelectionFromProvider("regiaoHidrograficaProvider");
        regiaoHidrografica.asAtrBootstrap().colPreference(12);
        regiaoHidrografica.asAtr().label("Região Hidrográfica");


        //2 -

        extremos = dadosGerais.addField("extremos", STypeComposite.class);
        extremos.asAtr().label("Linha de navegação");
        extremos.asAtrBootstrap().colPreference(12);

        extremo1 = extremos.addFieldComposite("extremo1");
        extremo1.asAtr().label("Extremo 1");

        extremo1pais = extremo1.addFieldString("extremo1pais");
        extremo1pais.withSelectionFromProvider("paisProvider");
        extremo1pais.asAtr().label("País");
        extremo1pais.asAtrBootstrap().colPreference(3);
        extremo1pais.setInitialValue("Brasil");

        extremo1uf = extremo1.addFieldString("extremo1uf");
        extremo1uf.asAtr().label("UF");
        extremo1uf.withSelectView();
        extremo1uf.selectionOf("AC", "AL", "AM", "AP", "BA", "CE", "DF", "ES", "GO", "MA", "MG", "MS", "MT", "PA", "PB", "PE", "PI", "PR", "RJ", "RN", "RO", "RR", "RS", "SC", "SE", "SP", "TO", "Outros");
        extremo1uf.asAtrBootstrap().colPreference(2);
        extremo1uf.asAtr().dependsOn(extremo1pais);
        extremo1uf.asAtr().exists(ins -> ins.findNearest(extremo1pais).filter(si -> "Brasil".equalsIgnoreCase(si.getValue())).isPresent());
        
        extremo1municipio = extremo1.addFieldString("extremo1municipio");
        extremo1municipio.withSelectionFromProvider("municipioDestinoProvider");
        extremo1municipio.asAtr().label("Município");
        extremo1municipio.asAtrBootstrap().colPreference(4);
        extremo1municipio.asAtr().dependsOn(extremo1pais);
        extremo1municipio.asAtr().exists( ins -> ins.findNearest(extremo1pais).filter(si -> "Brasil".equalsIgnoreCase(si.getValue())).isPresent());
        
        extremo1local = extremo1.addFieldString("extremo1local");
        extremo1local.asAtr().label("Local");
        extremo1local.asAtrBootstrap().colPreference(4);
        extremo1local.asAtr().dependsOn(extremo1pais);
        extremo1local.asAtr().exists( ins -> ins.findNearest(extremo1pais).filter(si -> !"Brasil".equalsIgnoreCase(si.getValue())).isPresent());
        
        
        extremo2 = extremos.addFieldComposite("extremo2");
        extremo2.asAtr().label("Extremo 2");
        
        extremo2pais = extremo2.addFieldString("extremo2pais");
        extremo2pais.withSelectionFromProvider("paisProvider");
        extremo2pais.asAtr().label("País");
        extremo2pais.asAtrBootstrap().colPreference(3);
        extremo2pais.setInitialValue("Brasil");
        
        extremo2uf = extremo2.addFieldString("extremo1uf");
        extremo2uf.asAtr().label("UF");
        extremo2uf.withSelectView();
        extremo2uf.selectionOf("AC","AL","AM","AP","BA","CE","DF","ES","GO","MA","MG","MS","MT","PA","PB","PE","PI","PR","RJ","RN","RO","RR","RS","SC","SE","SP","TO", "Outros");
        extremo2uf.asAtrBootstrap().colPreference(2);
        extremo2uf.asAtr().dependsOn(extremo2pais);
        extremo2uf.asAtr().exists( ins -> ins.findNearest(extremo2pais).filter(si -> "Brasil".equalsIgnoreCase(si.getValue())).isPresent());
        
        extremo2municipio = extremo2.addFieldString("extremo2municipio");
        extremo2municipio.withSelectionFromProvider("municipioDestinoProvider");
        extremo2municipio.asAtr().label("Município");
        extremo2municipio.asAtrBootstrap().colPreference(4);
        extremo2municipio.asAtr().dependsOn(extremo2pais);
        extremo2municipio.asAtr().exists( ins -> ins.findNearest(extremo2pais).filter(si -> "Brasil".equalsIgnoreCase(si.getValue())).isPresent());
        
        extremo2local = extremo2.addFieldString("extremo2local");
        extremo2local.asAtr().label("Local");
        extremo2local.asAtrBootstrap().colPreference(4);
        extremo2local.asAtr().dependsOn(extremo2pais);
        extremo2local.asAtr().exists( ins -> !ins.findNearest(extremo2pais).filter(si -> "Brasil".equalsIgnoreCase(si.getValue())).isPresent());
        
        //3 - 
        rios = dadosGerais.addFieldListOf("rios", STypeString.class);
        rios.withView(SViewListByTable::new);
        rios.withInitListener(list -> list.addNew());//USED by TestSDocumentRestoreMode
        rios.withMiniumSizeOf(STypeFormTest.QUANTIDADE_MINIMA);
        rios.asAtrBootstrap().colPreference(12);
        rios.asAtr().label("Rios");


        // 4 -


        //5 -

        frequencia = dadosGerais.addFieldInteger("frequencia");
        frequencia.asAtrBootstrap().colPreference(6);
        frequencia.asAtr().label("Frequência (Número de viagens por mês) ");


        //6 - Esquema Operacional 

        esquemasOperacionaisIda = addFieldListOf("esquemasOperacionaisIda", STypeAnotherComposisteChildListElement.class);
        
        esquemasOperacionaisVolta = addFieldListOf("esquemasOperacionaisVolta", STypeAnotherComposisteChildListElement.class);
        
    
        this.withView(new SViewByBlock(), v -> v.newBlock("Esquema Operacional Anexo")
                        .newBlock("Dados Gerais")
                        .add(dadosGerais)
                        .newBlock("Esquema Operacional Ida")
                        .add(esquemasOperacionaisIda)
                        .newBlock("Esquema Operacional Volta")
                        .add(esquemasOperacionaisVolta)
        );

    }
}
