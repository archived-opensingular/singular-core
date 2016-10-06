package org.opensingular.singular.server.commons.wicket.view.util;

import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;

public class DispatcherPageUtil {

    public static final String DISPATCHER_PAGE_PATH = "/";
    public static final String ACTION_ID = "a";
    public static final String FORM_ID = "k";
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherPageUtil.class);
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
            LOGGER.error(e.getMessage(), e);
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

        public DispatcherPageUrlAdditionalParamsBuilder params(Map<String, String> params) {
            params.forEach(this::param);
            return this;
        }

        public DispatcherPageUrlAdditionalParamsBuilder param(String name, Object value) {
            this.url += "&" + name + "=" + encodeParameter(value);
            return this;
        }

        public String build() {
            return this.url;
        }

    }

    public static String getBaseURL() {

        final RequestCycle requestCycle = RequestCycle.get();
        final Request      request      = requestCycle.getRequest();
        final String       currentPath  = request.getUrl().toString();

        String fullUrl = requestCycle.getUrlRenderer().renderFullUrl(request.getUrl());

        if (org.apache.commons.lang3.StringUtils.isNotBlank(currentPath)) {
            final int beginPath = fullUrl.lastIndexOf(currentPath);
            fullUrl = fullUrl.substring(0, beginPath - 1);
        }

        final Optional<String> contextPath = Optional.ofNullable(requestCycle.getRequest().getContextPath());
        final Optional<String> filterPath  = Optional.ofNullable(requestCycle.getRequest().getFilterPath());

        return fullUrl + contextPath.orElse("") + filterPath.orElse("");
    }
}
