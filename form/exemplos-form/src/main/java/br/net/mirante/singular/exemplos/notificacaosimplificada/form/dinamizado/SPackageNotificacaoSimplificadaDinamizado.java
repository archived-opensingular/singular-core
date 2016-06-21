/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.dinamizado;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.FormaFarmaceuticaBasica;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco.SPackageNotificacaoSimplificadaBaixoRisco;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeCategoriaRegulatoria;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.exemplos.util.PairConverter;
import br.net.mirante.singular.exemplos.util.TripleConverter;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeAttachmentList;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.converter.ValueToSICompositeConverter;
import br.net.mirante.singular.form.provider.Config;
import br.net.mirante.singular.form.provider.FilteredPagedProvider;
import br.net.mirante.singular.form.provider.ProviderContext;
import br.net.mirante.singular.form.type.core.SIInteger;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.view.SViewByBlock;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.view.SViewListByTable;
import br.net.mirante.singular.form.view.SViewSearchModal;
import br.net.mirante.singular.form.view.SViewTextArea;

public class SPackageNotificacaoSimplificadaDinamizado extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada.dinamizado";

    public SPackageNotificacaoSimplificadaDinamizado() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.loadPackage(SPackageNotificacaoSimplificadaBaixoRisco.class);
        pb.createType(STypeLinhaProducaoDinamizado.class);
        pb.createType(STypeNotificacaoSimplificadaDinamizado.class);
    }
}

