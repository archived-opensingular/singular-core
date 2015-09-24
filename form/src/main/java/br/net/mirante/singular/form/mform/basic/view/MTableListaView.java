package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoLista;

public class MTableListaView extends MView {

    private boolean permiteExclusaoDeLinha;
    private boolean permiteInsercaoDeLinha;
    private boolean permiteAdicaoDeLinha;

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return tipo instanceof MTipoLista;
    }

    public boolean isPermiteAdicaoDeLinha() {
        return permiteAdicaoDeLinha;
    }
    public boolean isPermiteExclusaoDeLinha() {
        return permiteExclusaoDeLinha;
    }
    public boolean isPermiteInsercaoDeLinha() {
        return permiteInsercaoDeLinha;
    }
    public MTableListaView withAdicaoDeLinha() {
        return setPermiteAdicaoDeLinha(true);
    }
    public MTableListaView withExclusaoDeLinha() {
        return setPermiteExclusaoDeLinha(true);
    }
    public MTableListaView withInsercaoDeLinha() {
        return setPermiteInsercaoDeLinha(true);
    }
    public MTableListaView setPermiteAdicaoDeLinha(boolean permiteAdicaoDeLinha) {
        this.permiteAdicaoDeLinha = permiteAdicaoDeLinha;
        return this;
    }
    public MTableListaView setPermiteExclusaoDeLinha(boolean permiteExclusaoDeLinha) {
        this.permiteExclusaoDeLinha = permiteExclusaoDeLinha;
        return this;
    }
    public MTableListaView setPermiteInsercaoDeLinha(boolean permiteInsercaoDeLinha) {
        this.permiteInsercaoDeLinha = permiteInsercaoDeLinha;
        return this;
    }
}
