package br.net.mirante.singular.pet.module.spring.security;

import br.net.mirante.singular.pet.module.wicket.PetApplication;
import org.apache.wicket.protocol.http.WicketFilter;

public class SecurityUtil {

    private SecurityUtil(){

    }

     public static String getLoginPath() {
        String path = PetApplication.get().getServletContext().getContextPath();
        path += PetApplication.get().getInitParameter(WicketFilter.FILTER_MAPPING_PARAM).replaceAll("\\*", "");
        path += "/login";
        return path;
    }

    public static String getLogoutPath() {
        String path = PetApplication.get().getServletContext().getContextPath();
        path += PetApplication.get().getInitParameter(WicketFilter.FILTER_MAPPING_PARAM).replaceAll("\\*", "");
        path += "/logout";
        return path;
    }
}
