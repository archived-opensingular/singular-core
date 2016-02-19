package br.net.mirante.singular.showcase.component.input.core;

import java.io.Serializable;

import br.net.mirante.singular.showcase.component.CaseBase;

public class CaseInputCoreYearMonth extends CaseBase implements Serializable {

    public CaseInputCoreYearMonth() {
        super("Date", "Mês/Ano");
        setDescriptionHtml("Componente para inserção de mês e ano.");
    }
}
