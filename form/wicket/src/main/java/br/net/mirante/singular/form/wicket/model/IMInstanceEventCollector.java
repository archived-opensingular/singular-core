package br.net.mirante.singular.form.wicket.model;

import java.util.List;

import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.mform.event.SInstanceEvent;

public interface IMInstanceEventCollector<I extends SInstance2> extends IMInstanciaAwareModel<I> {
    List<SInstanceEvent> getInstanceEvents();
    void clearInstanceEvents();
}
