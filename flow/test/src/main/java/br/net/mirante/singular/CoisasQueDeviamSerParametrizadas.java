package br.net.mirante.singular;

import br.net.mirante.singular.flow.core.AbstractProcessNotifiers;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.util.view.Lnk;

public class CoisasQueDeviamSerParametrizadas {

    public static final String PACKAGES_TO_SCAN = "br.net.mirante.singular";

    public static final MUser USER = new MUser() {
        @Override
        public Integer getCod() {
            return 1;
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
