package br.net.mirante.singular.showcase.component;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

public enum ShowCaseType {
    FORM,
    STUDIO;

    public static final String SHOWCASE_TYPE_PARAM = "tp";


    public static PageParameters buildPageParameters(ShowCaseType showCaseType){
        return buildPageParameters(null, showCaseType);
    }

    public static PageParameters buildPageParameters(String componentName){
        return buildPageParameters(componentName, null);
    }

    public static PageParameters buildPageParameters(String componentName, ShowCaseType showCaseType){
        final PageParameters pageParameters = new PageParameters();
        if (showCaseType == null) {
            final StringValue tipo = RequestCycle.get().getRequest().getRequestParameters().getParameterValue(SHOWCASE_TYPE_PARAM);
            if (!tipo.isNull()) {
                pageParameters.add(ShowCaseType.SHOWCASE_TYPE_PARAM, tipo);
            }
        } else {
            pageParameters.add(ShowCaseType.SHOWCASE_TYPE_PARAM, showCaseType.name());
        }

        if (StringUtils.isNotEmpty(componentName)) {
            pageParameters.add("cn", componentName);
        }
        return pageParameters;
    }
}
