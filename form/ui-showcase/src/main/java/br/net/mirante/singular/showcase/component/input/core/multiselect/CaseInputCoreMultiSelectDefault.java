package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.showcase.component.CaseBase;

import java.io.Serializable;

public class CaseInputCoreMultiSelectDefault extends CaseBase implements Serializable  {

    public CaseInputCoreMultiSelectDefault() {
        super("Multi Select", "Default");
        setDescriptionHtml("Se a view não for definida, então define o componente dependendo da quantidade de dados.");
    }

}
