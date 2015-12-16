package br.net.mirante.singular.form.mform;

import java.util.Collection;

public interface ICompositeType {

    public Collection<MTipo<?>> getContainedTypes();
}
