package br.net.mirante.singular.form.wicket.model;

import java.util.List;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.event.MInstanceEvent;

public interface IMInstanceEventCollector<I extends MInstancia> extends IMInstanciaAwareModel<I> {
    List<MInstanceEvent> getInstanceEvents();
    void clearInstanceEvents();
}
