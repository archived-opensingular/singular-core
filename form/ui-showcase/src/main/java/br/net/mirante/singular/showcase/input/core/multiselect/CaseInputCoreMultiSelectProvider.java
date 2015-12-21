package br.net.mirante.singular.showcase.input.core.multiselect;

import br.net.mirante.singular.showcase.CaseBase;

import java.io.Serializable;

public class CaseInputCoreMultiSelectProvider extends CaseBase implements Serializable {

    public CaseInputCoreMultiSelectProvider() {
        super("Multi Select", "Provedor Dinâmico");
        setDescriptionHtml("É permitido alterar o provedor de dados de forma que estes sejam carregados de forma dinâmica ou de outras fontes de informação.");
    }
}