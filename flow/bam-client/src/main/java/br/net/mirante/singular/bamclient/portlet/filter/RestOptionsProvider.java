package br.net.mirante.singular.bamclient.portlet.filter;

import org.apache.commons.lang3.StringUtils;

public @interface RestOptionsProvider {

    RestReturnType returnType() default RestReturnType.VALUE;
    String endpoint() default StringUtils.EMPTY;
}
