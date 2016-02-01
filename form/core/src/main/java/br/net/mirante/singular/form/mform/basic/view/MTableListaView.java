package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeLista;

public class MTableListaView extends MView {

    private boolean permiteAdicaoDeLinha   = true;
    private boolean permiteInsercaoDeLinha = false;
    private boolean permiteExclusaoDeLinha = true;

    @Override
    public boolean aplicavelEm(SType<?> tipo) {
        return tipo instanceof STypeLista;
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
