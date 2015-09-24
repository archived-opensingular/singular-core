package br.net.mirante.singular;

import br.net.mirante.singular.flow.core.AbstractProcessNotifiers;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.util.view.Lnk;
import br.net.mirante.singular.persistence.entity.Actor;

public class CoisasQueDeviamSerParametrizadas {

    public static final String PACKAGES_TO_SCAN = "br.net.mirante.singular";

    public static final Actor USER = new Actor() {
        @Override
        public Long getCod() {
            return 1l;
        }

        @Override
        public String getNomeGuerra() {
            return "Soldado Ryan";
        }

        @Override
        public String getEmail() {
            return "mirante.teste@gmail.com";
        }
    };


    public static final Lnk LINK_INSTANCE = new Lnk("Sei la", false);

    public static final Lnk LINK_TASK = new Lnk("Sei la de novo", false);

    public static final AbstractProcessNotifiers NOTIFIER = new NullNotifier();
}
