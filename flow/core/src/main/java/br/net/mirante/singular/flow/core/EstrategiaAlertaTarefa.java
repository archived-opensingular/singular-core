package br.net.mirante.singular.flow.core;

import java.io.Serializable;
import java.util.Set;

@SuppressWarnings("serial")
public abstract class EstrategiaAlertaTarefa implements Serializable {

    public abstract boolean isAlertaAplicavel(TaskInstance instanciaTarefa);

    public abstract String getDescricao(TaskInstance instanciaTarefa);

    public abstract Set<Integer> getPessoasAlvoAlerta(TaskInstance instanciaTarefa);
}
