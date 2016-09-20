/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.opas.gestaoobrasservicosaquisicoes.form;

import java.util.Collections;
import java.util.Optional;

import br.net.mirante.singular.exemplos.emec.credenciamentoescolagoverno.form.STypeEstado;
import br.net.mirante.singular.exemplos.emec.credenciamentoescolagoverno.form.STypeMunicipio;
import br.net.mirante.singular.exemplos.opas.gestaoobrasservicosaquisicoes.enums.AcaoGestaoObras;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewByBlock;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;

@SInfoType(spackage = SPackageGestaoObrasServicosAquisicoes.class)
public class STypeProcesso extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        addDadosProcesso();
        
        setView(SViewByBlock::new)
            .newBlock("Dados do Processo").add("dadosProcesso");
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
        aldeias.getElementsType().mockSelection();
        aldeias.withView(SViewListByMasterDetail::new)
            .asAtr().itemLabel("Aldeia");
    }
}
