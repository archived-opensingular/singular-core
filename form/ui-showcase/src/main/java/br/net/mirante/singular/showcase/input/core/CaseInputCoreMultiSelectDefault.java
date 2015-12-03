package br.net.mirante.singular.showcase.input.core;

import br.net.mirante.singular.showcase.CaseBase;

import java.io.Serializable;

public class CaseInputCoreMultiSelectDefault extends CaseBase implements Serializable  {

    public CaseInputCoreMultiSelectDefault() {
        super("Multi Select", "Default");
        setDescriptionHtml("Se a view não for definida, então define o componente dependendo da quantidade de dados.");
    }

}
