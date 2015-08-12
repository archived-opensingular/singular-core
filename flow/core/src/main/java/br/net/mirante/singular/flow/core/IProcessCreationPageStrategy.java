package br.net.mirante.singular.flow.core;

import java.io.Serializable;

import br.net.mirante.singular.flow.util.view.Lnk;

@FunctionalInterface
public interface IProcessCreationPageStrategy extends Serializable {

    public Lnk getCreatePageFor(ProcessDefinition<?> definicaoProcesso, MUser user);

}
