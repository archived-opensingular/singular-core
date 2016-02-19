package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.showcase.component.CaseBase;

public class CaseSimpleGrid extends CaseBase {

    public CaseSimpleGrid() {
        super("Grid", "Simple");
        String description = "";
        description += "Configura automaticamente o tamanho das colunas do bootstrap para telas menores, ";
        description += "multiplicando pelo fator de 2, 3 e 4 para colunas md (médium), sm (small) e xs (extra small), ";
        description += "mantendo o máximo de 12. ";
        description += "Por exemplo, ao configurar o tamanho para 3, o tamanho md será 6, sm 12 e xs 12.";

        setDescriptionHtml(description);
    }
}
