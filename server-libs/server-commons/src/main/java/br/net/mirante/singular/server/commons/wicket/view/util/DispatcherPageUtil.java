package br.net.mirante.singular.server.commons.wicket.view.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DispatcherPageUtil {

    public static final String DISPATCHER_PAGE_PATH = "/";
    public static final String ACTION_ID = "a";
    public static final String FORM_ID = "k";
    private static final Logger logger = LoggerFactory.getLogger(DispatcherPageUtil.class);
    private static final String ENCODING = "UTF-8";

    private String url;

    private DispatcherPageUtil(String url) {
        this.url = url;
    }

    public static DispatcherPageUtil baseURL(String baseURL) {
        return new DispatcherPageUtil(baseURL);
    }

    private static String encodeParameter(Object param) {
        try {
            return URLEncoder.encode(String.valueOf(param), ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            return "";
        }
    }

    public DispatcherPageUrlBuilder formAction(Object formAction) {
        return new DispatcherPageUrlBuilder(this.url + "?" + ACTION_ID + "=" + encodeParameter(formAction));
    }


    public static class DispatcherPageUrlBuilder {

        private String url;

        private DispatcherPageUrlBuilder(String url) {
            this.url = url;
        }

        public DispatcherPageUrlAdditionalParamsBuilder formId(Object formId) {
            if (!StringUtils.isEmpty(formId)) {
                return new DispatcherPageUrlAdditionalParamsBuilder(this.url + "&" + FORM_ID + "=" + encodeParameter(formId));
            }
            return new DispatcherPageUrlAdditionalParamsBuilder(this.url);
        }

    }

    public static class DispatcherPageUrlAdditionalParamsBuilder {
        private String url;

        private DispatcherPageUrlAdditionalParamsBuilder(String url) {
            this.url = url;
        }

        public DispatcherPageUrlAdditionalParamsBuilder param(String name, Object value) {
            this.url += "&" + name + "=" + encodeParameter(value);
            return this;
        }

        public String build() {
            return this.url;
        }

    }

}
