package br.net.mirante.singular.flow.core.builder;

import br.net.mirante.singular.flow.core.MProcessRole;

public interface BProcessRole<SELF extends BProcessRole<SELF>> {

    public MProcessRole getProcessRole();
}