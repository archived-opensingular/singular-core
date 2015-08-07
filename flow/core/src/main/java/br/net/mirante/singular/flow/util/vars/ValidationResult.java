package br.net.mirante.singular.flow.util.vars;

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
