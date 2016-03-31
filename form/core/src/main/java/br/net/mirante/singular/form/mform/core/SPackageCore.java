/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.core.annotation.STypeAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.STypeAnnotationList;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SPackageCore extends SPackage {

    public static final String NOME = "mform.core";




    public SPackageCore() {
        super(NOME);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        pb.createType(SType.class);
        pb.createType(STypeSimple.class);
        pb.createType(STypeList.class);
        pb.createType(STypeCode.class);
        pb.createType(STypePredicate.class);
        pb.createType(STypeString.class);
        pb.createType(STypeInteger.class);
        pb.createType(STypeBoolean.class);
        pb.createType(STypeDate.class);
        pb.createType(STypeDecimal.class);
        pb.createType(STypeMonetary.class);
        pb.createType(STypeDateTime.class);
        pb.createType(STypeTime.class);
        pb.createType(STypeComposite.class);
        pb.createType(STypeAnnotation.class);
        pb.createType(STypeAnnotationList.class);
        pb.createType(STypeFormula.class);
        pb.createType(STypeAttachment.class);
        pb.createType(STypeLatitudeLongitude.class);
        pb.createType(STypeAttachmentList.class);

        pb.createAttributeIntoType(STypeAttachment.class, STypeAttachment.ATR_ORIGINAL_ID);
        pb.createAttributeIntoType(STypeAttachment.class, STypeAttachment.ATR_IS_TEMPORARY);


    }

}