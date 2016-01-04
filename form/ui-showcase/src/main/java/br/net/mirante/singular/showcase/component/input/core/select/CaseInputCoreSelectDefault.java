package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.showcase.component.CaseBase;

import java.io.Serializable;

public class CaseInputCoreSelectDefault extends CaseBase implements Serializable {

    public CaseInputCoreSelectDefault() {
        super("Select", "Default");
        setDescriptionHtml("Se a view não for definida, então define o componente dependendo da quantidade de dados e da obrigatoriedade.");
    }

}
