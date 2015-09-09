package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class MTabView extends MView {

    private MTab tabDefault;

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return true;
    }

    public MTab addTab(String titulo) {
        MTab tab = new MTab(titulo);
        if (tabDefault == null) {
            tabDefault = tab;
        }
        return tab;
    }

    public MTab addTab(MTipo<?> tipo) {
        return addTab(tipo.as(AtrBasic.class).getLabel())
            .add(tipo);
    }

    public final class MTab {
        private final String titulo;
        private MTab(String titulo) {
            this.titulo = titulo;
        }
        public MTab add(MTipo<?> campo) {
            return this;
        }
        public String getTitulo() {
            return titulo;
        }
        public MTab setDefault() {
            MTabView.this.tabDefault = this;
            return this;
        }
    }
}
