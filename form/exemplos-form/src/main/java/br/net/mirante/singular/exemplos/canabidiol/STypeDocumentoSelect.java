/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.wicket.util.string.Strings;

import java.util.ArrayList;
import java.util.List;

@SInfoType(spackage = SPackagePeticaoCanabidiol.class)
public class STypeDocumentoSelect extends STypeString {


    private final static List<Pair> documentos = new ArrayList<>();

    static {
        documentos.add(Pair.of("55358721", Strings.capitalize("carteira de identidade (RG) expedida pela Secretaria de Segurança Pública de um dos estados da Federação ou Distrito Federal")));
        documentos.add(Pair.of("55358722", Strings.capitalize("cartão de identidade expedido por ministério ou órgão subordinado à Presidência da República, incluindo o Ministério da Defesa e os Comandos da Aeronáutica, da Marinha e do Exército")));
        documentos.add(Pair.of("55358723", Strings.capitalize("cartão de identidade expedido pelo poder judiciário ou legislativo, no nível federal ou estadual")));
        documentos.add(Pair.of("55358724", Strings.capitalize("carteira nacional de habilitação (modelo com fotografia)")));
        documentos.add(Pair.of("55358725", Strings.capitalize("carteira de trabalho")));
        documentos.add(Pair.of("55358726", Strings.capitalize("carteira de identidade emitida por conselho ou federação de categoria profissional, com fotografia e fé pública em todo território nacional")));
        documentos.add(Pair.of("55358727", Strings.capitalize("certidão de nascimento")));
        documentos.add(Pair.of("55358728", Strings.capitalize("passaporte nacional")));
        documentos.add(Pair.of("55358729", Strings.capitalize("outro documento de identificação com fotografia e fé pública")));
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        selectionOf(Pair.class)
                .id("${left}")
                .display("${right}")
                .converter(new SInstanceConverter<Pair, SIString>() {
                    @Override
                    public void fillInstance(SIString ins, Pair obj) {
                        ins.setValue(obj.getLeft());
                    }
                    @Override
                    public Pair toObject(SIString ins) {
                        return documentos.stream()
                                .filter(p -> p.getLeft().equals(ins.getValue())).findFirst().orElse(null);
                    }
                }).simpleProvider(i -> documentos);

    }
}
