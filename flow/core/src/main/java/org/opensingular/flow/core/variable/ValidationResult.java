/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core.variable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValidationResult {

    private List<String> erros;

    public boolean hasErros() {
        return erros != null && !erros.isEmpty();
    }

    public void addErro(String msg) {
        if (erros == null) {
            erros = new ArrayList<>();
        }
        erros.add(msg);
    }

    public void addErro(VarInstance var, String msg) {
        addErro(var.getNome() + ": " + msg);
    }

    @Override
    public String toString() {
        if (!hasErros()) {
            return "[]";
        }
        return erros.stream().collect(Collectors.joining("\n"));
    }

    public Stream<String> stream() {
        if (erros == null) {
            return (Stream) Collections.emptyList().stream();
        }
        return erros.stream();
    }
}
