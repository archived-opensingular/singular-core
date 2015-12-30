package br.net.mirante.singular.form.mform.basic.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class MTabView extends MView {

    private MTab tabDefault;

    private List<MTab> tabs = new ArrayList<>();

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return true;
    }

    public List<MTab> getTabs() {
        return tabs;
    }

    public MTab getTabDefault() {
        return tabDefault;
    }

    public MTab addTab(String titulo) {
        MTab tab = new MTab(this, titulo);
        if (tabDefault == null) {
            tabDefault = tab;
        }

        tabs.add(tab);
        return tab;
    }

    public MTab addTab(MTipo<?> tipo) {
        return addTab(tipo.as(AtrBasic.class).getLabel())
            .add(tipo);
    }

    public final static class MTab implements Serializable {

        private final MTabView tabView;
        private final String   titulo;
        private final List<String> nomesTipo;

        private MTab(MTabView tabView, String titulo) {
            this.tabView = tabView;
            this.titulo = titulo;
            nomesTipo = new ArrayList<>();
        }

        public MTab add(MTipo<?> campo) {
            nomesTipo.add(campo.getNomeSimples());
            return this;
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
