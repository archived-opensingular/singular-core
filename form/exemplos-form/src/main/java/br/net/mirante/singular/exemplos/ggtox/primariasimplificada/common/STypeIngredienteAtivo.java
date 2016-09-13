/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeLong;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewListByTable;

import java.util.UUID;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeIngredienteAtivo extends STypePersistentComposite {

    public static final String FIELD_NAME_ID              = "idAtivo";
    public static final String FIELD_NAME_NOME_COMUM_PTBR = "nomeComumPortugues";

    public STypeString                                         idAtivo;
    public STypeString                                         nomeQuimicoInternacional;
    public STypeString                                         nomeQuimico;
    public STypeString                                         nomeComum;
    public STypeString                                         nomeComumPortugues;
    public STypeString                                         entidadeAprovadora;
    public STypeLong                                           numeroCAS;
    public STypeString                                         grupoQuimico;
    public STypeComposite<SIComposite>                         sinonimia;
    public STypeList<STypeComposite<SIComposite>, SIComposite> sinonimias;
    public STypeString                                         nomeSinonimia;
    public STypeAttachmentList                                 formulasBrutasEstruturais;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this.withInitListener(si -> {
            si.getField(idAtivo).setValue(UUID.randomUUID().toString());
        });

        idAtivo = addFieldString("idAtivo");
        nomeQuimicoInternacional = addFieldString("nomeQuimicoInternacional");
        nomeQuimico = addFieldString("nomeQuimico");
        nomeComum = addFieldString("nomeComum");
        nomeComumPortugues = addFieldString(FIELD_NAME_NOME_COMUM_PTBR);
        entidadeAprovadora = addFieldString("entidadeAprovadora");
        numeroCAS = addField("numeroCAS", STypeLong.class);
        grupoQuimico = addFieldString("grupoQuimico");
        sinonimias = addFieldListOfComposite("sinonimias", "sinonimia");
        sinonimia = sinonimias.getElementsType();
        nomeSinonimia = sinonimia.addFieldString("nome");
        formulasBrutasEstruturais = addFieldListOfAttachment("formulasBrutasEstruturais", "formulaBrutaEstrutural");


        idAtivo
                .asAtr()
                .visible(false);

        nomeQuimicoInternacional
                .withTextAreaView()
                .asAtr()
                .label("Nome químico na grafia internacional (IUPAC)")
                .maxLength(1000)
                .asAtrBootstrap()
                .colPreference(6);

        nomeQuimico
                .withTextAreaView()
                .asAtr()
                .label("Nome químico em português (IUPAC)")
                .maxLength(1000)
                .asAtrBootstrap()
                .colPreference(6);

        nomeComum
                .asAtr()
                .label("Nome comum (ISO, ANSI, BSI)")
                .asAtrBootstrap()
                .colPreference(6);

        nomeComumPortugues
                .asAtr()
                .label("Nome comum em português")
                .asAtrBootstrap()
                .colPreference(6);

        entidadeAprovadora
                .asAtr()
                .label("Entidade aprovadora do nome em português")
                .asAtrBootstrap()
                .colPreference(4);

        numeroCAS
                .asAtr()
                .label("Nº no chemical abstract service registry (CAS)")
                .maxLength(15)
                .asAtrBootstrap()
                .colPreference(4);

        grupoQuimico
                .asAtr()
                .label("Grupo químico em português (Usar letras maiúsculas)")
                .maxLength(30)
                .asAtrBootstrap()
                .colPreference(4);

        sinonimias
                .asAtr()
                .label("Sinonímias");
        sinonimias
                .withView(SViewListByTable::new);


        nomeSinonimia
                .asAtr()
                .maxLength(50)
                .label("Nome");

        formulasBrutasEstruturais
                .asAtr()
                .label("Fórmula bruta e estrutural");
    }
}
