package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInstance;

import java.io.Serializable;
import java.util.List;

public interface SimpleProvider<E extends Serializable> extends Provider<E> {

    List<E> load(SInstance ins);

}