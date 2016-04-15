package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInstance;

import java.io.Serializable;
import java.util.List;

public interface PagedResultProvider<R> extends Serializable {

    Long getSize(SInstance filter);

    List<R> load(SInstance filter, long first, long count);

}