/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.client.portlet.filter;

import org.apache.commons.lang3.StringUtils;

public @interface RestOptionsProvider {

    RestReturnType returnType() default RestReturnType.VALUE;
    String endpoint() default StringUtils.EMPTY;
}
