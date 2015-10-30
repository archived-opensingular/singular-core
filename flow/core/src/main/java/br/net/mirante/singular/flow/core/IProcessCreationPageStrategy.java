package br.net.mirante.singular.flow.core;

import java.io.Serializable;

import br.net.mirante.singular.flow.core.view.Lnk;

@FunctionalInterface
public interface IProcessCreationPageStrategy extends Serializable {

    public Lnk getCreatePageFor(ProcessDefinition<?> definicaoProcesso, MUser user);

}
