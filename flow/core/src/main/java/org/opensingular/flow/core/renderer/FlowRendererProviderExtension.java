package org.opensingular.flow.core.renderer;

import org.opensingular.flow.core.FlowInstance;
import org.opensingular.lib.commons.extension.SingularExtension;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Represents a extension point (see {@link SingularExtension}) for the implementation of automatic generator of
 * diagrams for flow definitions (see {@link IFlowRenderer}).
 */
public interface FlowRendererProviderExtension extends SingularExtension, Serializable {

    /** Instantiates the renders implementation. */
    @Nonnull
    public IFlowRenderer getRenderer();

    @Nonnull
    public default byte[] generateHistoryImage(@Nonnull FlowInstance flowInstance) {
        return getRenderer().generatePng(flowInstance.getFlowDefinition(),
                ExecutionHistoryForRendering.from(flowInstance));
    }

}
