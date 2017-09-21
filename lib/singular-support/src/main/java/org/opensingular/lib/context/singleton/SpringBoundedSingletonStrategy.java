package org.opensingular.lib.context.singleton;

import org.opensingular.lib.commons.context.singleton.ContextBoundedSingletonStrategy;
import org.springframework.context.annotation.Lazy;

/**
 *
 */
@Lazy(false)
public class SpringBoundedSingletonStrategy extends ContextBoundedSingletonStrategy {

}