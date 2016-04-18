/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewSearchModal;
import br.net.mirante.singular.form.mform.converter.ValueToSInstanceConverter;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.FilteredPagedProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CaseInputCoreSelectSearchPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        final STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        final STypeComposite funcionario = tipoMyForm.addFieldComposite("funcionario");
        funcionario.asAtrBasic().label("Funcionario").displayString("${nome} - ${graduacao}");

        final STypeString nome  = funcionario.addFieldString("nome");
        final STypeString cargo = funcionario.addFieldString("graduacao");

        funcionario.withView(new SViewSearchModal().title("Buscar Profissionais"))
                .asAtrProvider()
                .provider(new FuncionarioProvider())
                .converter(new ValueToSInstanceConverter<Pair>() {
                    @Override
                    public void toInstance(SInstance newFunc, Pair pair) {
                        ((SIComposite) newFunc).setValue(nome, pair.getLeft());
                        ((SIComposite) newFunc).setValue(cargo, pair.getRight());
                    }
                });

    }

    private static class FuncionarioProvider implements FilteredPagedProvider<Pair> {

        @Override
        public void loadFilterDefinition(STypeComposite<?> filter) {
            filter.addFieldString("nome").asAtrBasic().label("Nome").asAtrBootstrap().colPreference(6);
            filter.addFieldString("graduacao").asAtrBasic().label("Graduação").asAtrBootstrap().colPreference(6);
        }

        @Override
        public List<Column> getColumns() {
            return Arrays.asList(Column.of("left", "Nome"), Column.of("right", "Graduação"));
        }

        @Override
        public Long getSize(SInstance root, SInstance filter) {
            return 3L;
        }

        @Override
        public List<Pair> load(SInstance root, SInstance filter, long first, long count) {
            List<Pair> pairs = new ArrayList<>();

            pairs.add(Pair.of("Danilo", "Engenheria da computação"));
            pairs.add(Pair.of("Vinicius", "Ciência da computação"));
            pairs.add(Pair.of("Delfino", "Ciência da computação"));

            if (Value.of(filter, "nome") != null) {
                pairs = pairs
                        .stream()
                        .filter(p -> ((String) p.getLeft()).toUpperCase().contains(Value.of(filter, "nome").toString().toUpperCase()))
                        .collect(Collectors.toList());
            }

            if (Value.of(filter, "graduacao") != null) {
                pairs = pairs
                        .stream()
                        .filter(p -> ((String) p.getRight()).toUpperCase().contains(Value.of(filter, "graduacao").toString().toUpperCase()))
                        .collect(Collectors.toList());
            }

            return pairs;
        }

    }


}
