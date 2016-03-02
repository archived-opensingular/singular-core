package br.net.mirante.singular.pet.module.wicket.view.util.dispatcher;

import org.springframework.util.StringUtils;

public class DispatcherPageUtil {

    public static final String DISPATCHER_PAGE_PATH = "/";
    public static final String ACTION_ID = "a";
    public static final String FORM_ID = "k";
    private static final String ENCODING = "UTF-8";


    public static String buildUrl(String baseURL, Object formId, Object formAction) {
        String base = baseURL + "?" + ACTION_ID + "=" + formAction;
        if (!StringUtils.isEmpty(formId)) {
            base += "&" + FORM_ID + "=" + formId;
        }
        return base;
    }

}
