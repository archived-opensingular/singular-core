package org.opensingular.flow.core.renderer;

/**
 * Identifies the content type a {@link ExecutionHistoryForRendering}.
 *
 * @author Daniel C. Bordin
 * @since 2018-08-28
 */
public enum ExecutionHistoryType {
    EMPTY, INSTANCE_EXECUTION, MULTI_EXECUTION_BY_DURATION, MULTI_EXECUTION_BY_QUANTITY, MULTI_EXECUTION_BY_STATE,
    MULTI_EXECUTION_BY_TOTAL_DURATION
}
