/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.examples;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.*;
import br.net.mirante.singular.form.type.country.brazil.STypeCNPJ;
import br.net.mirante.singular.form.type.country.brazil.STypeCPF;
import br.net.mirante.singular.form.type.country.brazil.STypeTelefoneNacional;
import br.net.mirante.singular.form.type.util.STypeEMail;
import br.net.mirante.singular.form.type.util.STypePersonName;
import br.net.mirante.singular.form.type.util.STypeYearMonth;
import br.net.mirante.singular.form.view.SMultiSelectionBySelectView;
import br.net.mirante.singular.form.view.SViewListByForm;
import br.net.mirante.singular.form.view.SViewListByTable;
import br.net.mirante.singular.form.view.SViewTab;

public class SPackageCurriculo extends SPackage {

    public static final String PACOTE         = "mform.exemplo.curriculo";

    public SPackageCurriculo() {
        super("mform.exemplo.curriculo");
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
       pb.createType(STypeCurriculo.class);
    }
}
