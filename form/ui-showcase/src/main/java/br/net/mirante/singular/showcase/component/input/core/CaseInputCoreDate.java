package br.net.mirante.singular.showcase.component.input.core;

import java.io.Serializable;

import br.net.mirante.singular.showcase.component.CaseBase;

public class CaseInputCoreDate extends CaseBase implements Serializable {

    public CaseInputCoreDate() {
        super("Date", "Simples");
        setDescriptionHtml("Componente para inserção de data");
    }
}
