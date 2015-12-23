package br.net.mirante.singular.form.mform.basic.view;

import java.io.Serializable;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class MTabView extends MView {

    private MTab tabDefault;

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return true;
    }

    public MTab addTab(String titulo) {
        MTab tab = new MTab(this, titulo);
        if (tabDefault == null) {
            tabDefault = tab;
        }
        return tab;
    }

    public MTab addTab(MTipo<?> tipo) {
        return addTab(tipo.as(AtrBasic.class).getLabel())
            .add(tipo);
    }

    public final static class MTab implements Serializable {
        private final MTabView tabView;
        private final String   titulo;
        private MTab(MTabView tabView, String titulo) {
            this.tabView = tabView;
            this.titulo = titulo;
        }
        public MTab add(MTipo<?> campo) {
            return this;
        }
        public String getTitulo() {
            return titulo;
        }
        public MTab setDefault() {
            tabView.tabDefault = this;
            return this;
        }
    }
}
