/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeString;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeIngredienteAtivo extends STypeComposite<SIComposite> {

    public STypeString                                         nomeQuimicoInternacional;
    public STypeString                                         nomeQuimico;
    public STypeString                                         nomeComum;
    public STypeString                                         nomeComumPortugues;
    public STypeString                                         entidadeAprovadora;
    public STypeString                                         numeroCAS;
    public STypeString                                         grupoQuimico;
    public STypeComposite<SIComposite>                         sinonimia;
    public STypeList<STypeComposite<SIComposite>, SIComposite> sinonimias;
    public STypeString                                         nomeSinonimia;
    public STypeAttachmentList                                 formulasBrutasEstruturais;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        nomeQuimicoInternacional = addFieldString("nomeQuimicoInternacional");
        nomeQuimico = addFieldString("nomeQuimico");
        nomeComum = addFieldString("nomeComum");
        nomeComumPortugues = addFieldString("nomeComumPortugues");
        entidadeAprovadora = addFieldString("entidadeAprovadora");
        numeroCAS = addFieldString("numeroCAS");
        grupoQuimico = addFieldString("grupoQuimico");
        sinonimias = addFieldListOfComposite("sinonimias", "sinonimia");
        sinonimia = sinonimias.getElementsType();
        nomeSinonimia = sinonimia.addFieldString("nome");
        formulasBrutasEstruturais = addFieldListOfAttachment("formulasBrutasEstruturais", "formulaBrutaEstrutural");

        nomeQuimicoInternacional
                .asAtr()
                .label("Nome químico na grafia internacional (IUPAC)")
                .asAtrBootstrap()
                .colPreference(6);

        nomeQuimico
                .asAtr()
                .label("Nome químico em português (IUPAC)")
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
                .asAtrBootstrap()
                .colPreference(4);

        grupoQuimico
                .asAtr()
                .label("Grupo químico em português (Usar letras maiúsculas)")
                .asAtrBootstrap()
                .colPreference(4);

        sinonimias
                .asAtr()
                .label("Sinonímias")
                .asAtrBootstrap();

        nomeSinonimia
                .asAtr()
                .label("Nome");

        formulasBrutasEstruturais
                .asAtr()
                .label("Fórmula bruta e estrutural");
    }
}
