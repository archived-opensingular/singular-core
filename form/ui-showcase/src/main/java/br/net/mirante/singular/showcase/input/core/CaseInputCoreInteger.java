package br.net.mirante.singular.showcase.input.core;

import br.net.mirante.singular.showcase.CaseBase;
import java.io.Serializable;

public class CaseInputCoreInteger extends CaseBase implements Serializable {

    public CaseInputCoreInteger() {
        super("Numeric", "Integer");
        setDescriptionHtml("Campo para edição de dados inteiro");
    }

}
