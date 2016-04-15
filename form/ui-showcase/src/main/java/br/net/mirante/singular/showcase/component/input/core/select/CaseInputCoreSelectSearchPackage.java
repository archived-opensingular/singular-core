/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.view.SViewSearchModal;
import br.net.mirante.singular.form.mform.basic.view.SViewSearchModal.Column;
import br.net.mirante.singular.form.mform.provider.ValueToSICompositeConverter;
import br.net.mirante.singular.form.mform.provider.PagedResultProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CaseInputCoreSelectSearchPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        final STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        final STypeComposite funcionario = tipoMyForm.addFieldComposite("funcionario");
        funcionario.asAtrBasic().label("Funcionario").displayString("${nome} - ${cargo}");
        funcionario.addFieldString("nome");
        funcionario.addFieldString("cargo");

        funcionario.withView(new SViewSearchModal()
                .withTitle("Buscar Profissionais")
                .withFilter(filter -> {
                    filter.addFieldString("nome")
                            .asAtrBasic().label("Nome")
                            .asAtrBootstrap().colPreference(6);
                    filter.addFieldString("cargo")
                            .asAtrBasic().label("Cargo")
                            .asAtrBootstrap().colPreference(6);
                })
                .withProvider(new MyProvider())
                .withConverter((ValueToSICompositeConverter<Pair>) (newFunc, pair) -> {
                    newFunc.setValue("nome", pair.getLeft());
                    newFunc.setValue("cargo", pair.getRight());
                })
                .withColumns(
                        Column.of("left", "Nome"),
                        Column.of("right", "Cargo")
                )
        );

    }

    private static class MyProvider implements PagedResultProvider<Pair> {

        @Override
        public Long getSize(SInstance filter) {
            return 3L;
        }

        @Override
        public List<Pair> load(SInstance filter, long first, long count) {
            List<Pair> pairs = new ArrayList<>();

            pairs.add(Pair.of("Danilo", "Engenheiro da Computação"));
            pairs.add(Pair.of("Vinicius", "Cientista da computação"));
            pairs.add(Pair.of("Delfino", "Cientista da computação"));

            if (Value.of(filter, "nome") != null) {
                pairs = pairs
                        .stream()
                        .filter(p -> ((String) p.getLeft()).toUpperCase().contains(Value.of(filter, "nome").toString().toUpperCase()))
                        .collect(Collectors.toList());
            }

            if (Value.of(filter, "cargo") != null) {
                pairs = pairs
                        .stream()
                        .filter(p -> ((String) p.getRight()).toUpperCase().contains(Value.of(filter, "cargo").toString().toUpperCase()))
                        .collect(Collectors.toList());
            }

            return pairs;
        }
    }


}
