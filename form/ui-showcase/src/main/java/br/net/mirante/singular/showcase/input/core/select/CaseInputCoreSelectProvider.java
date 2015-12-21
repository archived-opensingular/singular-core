package br.net.mirante.singular.showcase.input.core.select;

import br.net.mirante.singular.showcase.CaseBase;

import java.io.Serializable;

public class CaseInputCoreSelectProvider extends CaseBase implements Serializable {
    public CaseInputCoreSelectProvider() {
        super("Select", "Provedor Dinâmico");
        setDescriptionHtml("É permitido alterar o provedor de dados de forma que estes sejam carregados de forma dinâmica ou de outras fontes de informação.");
    }
}
