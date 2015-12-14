package br.net.mirante.singular.showcase.input.core;

import br.net.mirante.singular.showcase.CaseBase;

import java.io.Serializable;

public class CaseInputCoreSelectDefault extends CaseBase implements Serializable {

    public CaseInputCoreSelectDefault() {
        super("Select", "Default");
        setDescriptionHtml("Se a view não for definida, então define o componente dependendo da quantidade de dados e da obrigatoriedade.");
    }

}
