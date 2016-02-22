package br.net.mirante.singular.exemplos.canabidiol.model;

import java.util.List;

public class CapituloCID extends AbstractDadoCID {

    private Integer capitulo;

    private List<GrupoCID> grupos;

    public Integer getCapitulo() {
        return capitulo;
    }

    public void setCapitulo(Integer capitulo) {
        this.capitulo = capitulo;
    }

    public List<GrupoCID> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoCID> grupos) {
        this.grupos = grupos;
    }
}
