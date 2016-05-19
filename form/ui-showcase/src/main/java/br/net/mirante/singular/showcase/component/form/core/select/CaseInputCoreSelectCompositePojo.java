package br.net.mirante.singular.showcase.component.form.core.select;


import java.io.Serializable;

import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;

public class CaseInputCoreSelectCompositePojo extends CaseBase implements Serializable {

    public CaseInputCoreSelectCompositePojo() {
        super("Select", "Tipo composto com objetos serializaveis.");
        setDescriptionHtml("É possivel utilizar objetos serializaveis para realizar a seleção, porem neste caso, é necessario informar o conversor.");
        getAditionalSources().add(ResourceRef.forSource(IngredienteQuimico.class).orElse(null));
        getAditionalSources().add(ResourceRef.forSource(IngredienteQuimicoFilteredProvider.class).orElse(null));
    }

}
