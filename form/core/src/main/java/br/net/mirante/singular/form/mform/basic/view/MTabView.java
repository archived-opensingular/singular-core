package br.net.mirante.singular.form.mform.basic.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class MTabView extends MView {

    private MTab tabDefault;

    private List<MTab> tabs = new ArrayList<>();

    @Override
    public boolean aplicavelEm(SType<?> tipo) {
        return true;
    }

    public List<MTab> getTabs() {
        return tabs;
    }

    public MTab getTabDefault() {
        return tabDefault;
    }

    public MTab addTab(String id, String titulo) {
        MTab tab = new MTab(this, id, titulo);
        if (tabDefault == null) {
            tabDefault = tab;
        }

        tabs.add(tab);
        return tab;
    }

    public MTab addTab(SType<?> tipo) {
        return addTab(tipo.getSimpleName(), tipo.as(AtrBasic.class).getLabel())
            .add(tipo);
    }

    public final static class MTab implements Serializable {

        private final MTabView tabView;
        private final String   id;
        private final String   titulo;
        private final List<String> nomesTipo;

        private MTab(MTabView tabView, String id, String titulo) {
            this.tabView = tabView;
            this.id = id;
            this.titulo = titulo;
            nomesTipo = new ArrayList<>();
        }

        public MTab add(SType<?> campo) {
            nomesTipo.add(campo.getSimpleName());
            return this;
        }

        public String getId() {
            return id;
        }

        public String getTitulo() {
            return titulo;
        }

        public List<String> getNomesTipo() {
            return nomesTipo;
        }

        public MTab setDefault() {
            tabView.tabDefault = this;
            return this;
        }
    }
}
