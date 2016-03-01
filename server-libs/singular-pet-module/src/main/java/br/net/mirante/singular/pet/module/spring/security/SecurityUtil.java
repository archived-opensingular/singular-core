package br.net.mirante.singular.pet.module.spring.security;

import br.net.mirante.singular.pet.module.wicket.PetApplication;
import br.net.mirante.singular.pet.module.wicket.PetSession;

public class SecurityUtil {

    private SecurityUtil() {

    }

    public static String getLoginPath() {
        return PetApplication.get().getServletContext().getContextPath() +
                PetSession.get().getUserDetails().getServerContext().getUrlPath() +
                "/login";
    }

    public static String getLogoutPath() {
        return PetApplication.get().getServletContext().getContextPath() +
                PetSession.get().getUserDetails().getServerContext().getUrlPath() +
                "/logout";
    }
}
