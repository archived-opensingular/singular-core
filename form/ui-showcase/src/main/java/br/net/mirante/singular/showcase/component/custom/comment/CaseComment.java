package br.net.mirante.singular.showcase.component.custom.comment;

import br.net.mirante.singular.showcase.component.CaseBase;

import java.io.Serializable;

public class CaseComment  extends CaseBase implements Serializable {

    public CaseComment() {
        super("Annotation");
        setDescriptionHtml("Anotações e comentários associados a elementos de um form");
    }
}
