package br.net.mirante.singular.showcase.view.page.form.examples.canabidiol.model;

import java.util.List;

public class GrupoCID extends AbstractDadoCID {

    private List<CategoriaCID> categorias;

    public List<CategoriaCID> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<CategoriaCID> categorias) {
        this.categorias = categorias;
    }
}
