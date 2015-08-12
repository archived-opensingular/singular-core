package br.net.mirante.singular.form.mform.basic.view;

import org.apache.commons.lang.NotImplementedException;

import br.net.mirante.singular.form.mform.MTipo;

public class MTabView implements MView {

    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return true;
    }

    public MTab addTab(String titulo, boolean tabDefault) {
        // TODO implementar
        throw new NotImplementedException();
    }

    public MTab addTab(String titulo) {
        return addTab(titulo, false);
    }

    public MTab addTab(MTipo tipo) {
        // TODO implementar
        throw new NotImplementedException();
    }

    public MTabView withNaoDefinidosVaoParaTabDoIrmaoAnterior() {
        return this;
    }

    public MTabView withNaoDefinidosVaoParaTabDefault() {
        return this;
    }

    public final class MTab {

        private MTab() {
        }

        ;

        public MTab add(MTipo<?> campo) {
            return this;
        }

    }
}
