package br.net.mirante.singular.pet.commons.spring.security;


import br.net.mirante.singular.pet.commons.wicket.PetApplication;
import br.net.mirante.singular.pet.commons.wicket.PetSession;

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
