package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.showcase.component.CaseBase;
import java.io.Serializable;

public class CaseInputCoreDate extends CaseBase implements Serializable {

    public CaseInputCoreDate() {
        super("Date", "Simples");
        setDescriptionHtml("Componente para inserção de data através da seleção por calendário ou texto.");
    }
}
