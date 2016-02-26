package br.net.mirante.singular.pet.module.wicket.view.form;

import org.springframework.util.StringUtils;

public class FormPageUtil {

    public static final String FORM_PAGE_PATH = "/form";
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
