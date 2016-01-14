package br.net.mirante.singular.showcase.component.custom.comment;

import br.net.mirante.singular.showcase.component.CaseBase;

import java.io.Serializable;

public class CaseAnnotation extends CaseBase implements Serializable {

    public CaseAnnotation() {
        super("Annotation");
        setDescriptionHtml("Anotações e comentários associados a elementos de um form");
    }
}
