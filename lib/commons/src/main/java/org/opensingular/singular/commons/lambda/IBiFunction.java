/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.commons.lambda;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface IBiFunction<T, U, R> extends BiFunction<T, U, R>, Serializable {

}
