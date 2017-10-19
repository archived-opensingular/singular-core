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

package org.opensingular.form.util.transformer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SISimple;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestVal {

    private static final String DESCRICAO  = "Descrição Teste";
    private static final Date   DT_INICIAL = new Date();
    private static final Date   DT_FINAL   = new Date();
    private static final Date   DT_1       = new Date();
    private static final Date   DT_2       = new Date();
    private static final Date   DT_3       = new Date();
    private SDictionary dictionary;
    private STypeComposite<? extends SIComposite>               _raiz;
    private STypeString                                         _descricao;
    private STypeComposite<SIComposite>                         _periodo;
    private STypeDate                                           _dataInicial;
    private STypeDate                                           _dataFinal;
    private STypeList<STypeComposite<SIComposite>, SIComposite> _alertas;
    private STypeComposite<SIComposite>                         _alerta;
    private STypeDate                                           _alerta_data;
    private SIComposite                                         evento;
    private SIList<SIComposite>                                 alertas;
    private Map<String, Value.Content> valorEsperado = new LinkedHashMap<>();
    private SIList      listaAlertas;
    private SIComposite alerta3;
    private SIComposite alertaVazio;
    private SIComposite alerta2;
    private SIComposite alerta1;
    private SISimple    dataVazia;
    private SISimple    data3;

    @Before
    public void setUp() {
        dictionary = SDictionary.create();
        PackageBuilder pb = dictionary.createNewPackage("teste");

        _raiz = pb.createCompositeType("_raiz");
        _descricao = _raiz.addField("descricao", STypeString.class);
        _periodo = _raiz.addFieldComposite("periodo");
        _dataInicial = _periodo.addField("dataInicial", STypeDate.class);
        _dataFinal = _periodo.addField("dataFinal", STypeDate.class);
        _alertas = _periodo.addFieldListOfComposite("alertas", "alerta");
        _alerta = _alertas.getElementsType();
        _alerta_data = _alerta.addField("data", STypeDate.class);

        _raiz.asAtr().label("Evento");
        _descricao.asAtr().label("Descrição");
        // *** período não possui label
        _dataInicial.asAtr().label("Data inicial");
        _dataFinal.asAtr().label("Data final");
        _alertas.asAtr().label("Alertas");
        _alerta.asAtr().label("Alerta");
        _alerta_data.asAtr().label("Data");

        evento = _raiz.newInstance();


        // descricao
        evento.setValue(_descricao, DESCRICAO);
        valorEsperado.put(_descricao.getNameSimple(), new Value.Content(DESCRICAO, _descricao.getName()));
        // perido
        SIComposite periodo = (SIComposite) evento.getField(_periodo.getNameSimple());
        valorEsperado.put(_periodo.getNameSimple(), new Value.Content(new LinkedHashMap<String, Object>(), _periodo.getName()));
        //dt inicial
        periodo.setValue(_dataInicial, DT_INICIAL);
        ((Map<String, Value.Content>) valorEsperado.get(_periodo.getNameSimple()).getRawContent()).put(_dataInicial.getNameSimple(), new Value.Content(DT_INICIAL, _dataInicial.getName()));
        //dt final
        periodo.setValue(_dataFinal, DT_FINAL);
        ((Map<String, Value.Content>) valorEsperado.get(_periodo.getNameSimple()).getRawContent()).put(_dataFinal.getNameSimple(), new Value.Content(DT_FINAL, _dataFinal.getName()));
        //alertas
        listaAlertas = (SIList) periodo.getField(_alertas.getNameSimple());
        ((Map<String, Value.Content>) valorEsperado.get(_periodo.getNameSimple()).getRawContent()).put(_alertas.getNameSimple(), new Value.Content(new ArrayList<Value.Content>(), _alertas.getName()));

        //Alerta Data 1
        alerta1 = _alerta.newInstance();
        alerta1.getField(_alerta_data.getNameSimple()).setValue(DT_1);
        listaAlertas.addElement(alerta1);
        Map<String, Value.Content> alertaMap1 = new LinkedHashMap<>();
        alertaMap1.put(_alerta_data.getNameSimple(), new Value.Content(DT_1, _alerta_data.getName()));
        ((List<Value.Content>) ((Map<String, Value.Content>) valorEsperado.get(_periodo.getNameSimple()).getRawContent()).get(_alertas.getNameSimple()).getRawContent()).add(new Value.Content((Serializable) alertaMap1, alerta1.getType().getName()));

        //Alerta Data 2
        alerta2 = _alerta.newInstance();
        alerta2.getField(_alerta_data.getNameSimple()).setValue(DT_2);
        listaAlertas.addElement(alerta2);
        Map<String, Value.Content> alertaMap2 = new LinkedHashMap<>();
        alertaMap2.put(_alerta_data.getNameSimple(), new Value.Content(DT_2, _alerta_data.getName()));
        ((List<Value.Content>) ((Map<String, Value.Content>) valorEsperado.get(_periodo.getNameSimple()).getRawContent()).get(_alertas.getNameSimple()).getRawContent()).add(new Value.Content((Serializable) alertaMap2, alerta2.getType().getName()));

        //Alerta Data 3
        alerta3 = _alerta.newInstance();
        data3 = (SISimple) alerta3.getField(_alerta_data.getNameSimple());
        data3.setValue(DT_3);
        listaAlertas.addElement(alerta3);
        Map<String, Value.Content> alertaMap3 = new LinkedHashMap<>();
        alertaMap3.put(_alerta_data.getNameSimple(), new Value.Content(DT_3, _alerta_data.getName()));
        ((List<Value.Content>) ((Map<String, Value.Content>) valorEsperado.get(_periodo.getNameSimple()).getRawContent()).get(_alertas.getNameSimple()).getRawContent()).add(new Value.Content((Serializable) alertaMap3, alerta3.getType().getName()));

        //Alerta Vazio
        alertaVazio = _alerta.newInstance();
        dataVazia = (SISimple) alertaVazio.getField(_alerta_data.getNameSimple());
        dataVazia.setValue(null);

        listaAlertas.addElement(alertaVazio);
        Map<String, Value.Content> alertaVazioMap = new LinkedHashMap<>();
        alertaVazioMap.put(_alerta_data.getNameSimple(), new Value.Content(null, _alerta_data.getName()));
        ((List<Value.Content>) ((Map<String, Value.Content>) valorEsperado.get(_periodo.getNameSimple()).getRawContent()).get(_alertas.getNameSimple()).getRawContent()).add(new Value.Content((Serializable) alertaVazioMap, alertaVazio.getType().getName()));

    }

    @Test
    public void testDehydrate() {
        Assert.assertEquals(valorEsperado, Value.dehydrate(evento).getRawContent());
    }

    @Test
    public void testHydrate() {
        SIComposite newInstance = _raiz.newInstance();

        Value.hydrate(newInstance, new Value.Content((Serializable) valorEsperado, newInstance.getName()));
        Assert.assertEquals(evento, newInstance);
    }

    @Test
    public void testHydrateDehydrate() {
        SIComposite newInstance = _raiz.newInstance();
        Value.hydrate(newInstance, new Value.Content((Serializable) valorEsperado, newInstance.getName()));
        Assert.assertEquals(valorEsperado, Value.dehydrate(newInstance).getRawContent());
    }


    @Test
    public void testDehydrateHydrate() {
        Object    value         = Value.dehydrate(evento);
        SInstance newInstance = _raiz.newInstance();
        Value.hydrate(newInstance, new Value.Content((Serializable) valorEsperado, newInstance.getName()));
        Assert.assertEquals(evento, newInstance);
    }

    @Test
    public void testNoNull() {
        Assert.assertTrue(Value.notNull(evento));
        Assert.assertTrue(Value.notNull(evento, _alerta_data));
        Assert.assertTrue(Value.notNull(evento, _alerta));
        Assert.assertTrue(Value.notNull(listaAlertas));
    }

    @Test
    public void testValOf() {
        Assert.assertEquals(DT_1, Value.of(alerta1, _alerta_data));
        Assert.assertEquals(DT_2, Value.of(alerta2, _alerta_data));
        Assert.assertEquals(DT_3, Value.of(alerta3, _alerta_data));
        Assert.assertEquals(null, Value.of(alertaVazio, _alerta_data));
        Assert.assertEquals((Date) null, Value.of(dataVazia));
        Assert.assertEquals(DT_3, Value.of(data3));
    }

    @Test
    public void testNullSafe() {
        Value.of((SISimple) null);
        Value.of(null, (String) null);
        Value.of(null, (STypeSimple) null);
        Value.notNull((SIList) null);
        Value.notNull((SInstance) null, (STypeSimple) null);
        Value.notNull((SInstance) null, (STypeComposite) null);
        Value.notNull((SInstance) null, (STypeList) null);
        Value.dehydrate((SIComposite) null);
        Value.hydrate((SIComposite) null, null);
    }

}
