package org.opensingular.flow.core.renderer;

import org.opensingular.lib.commons.extension.SingularExtension;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Represents a extension point (see {@link SingularExtension}) for the implementation of automatic generator of
 * diagrams for flow definitions (see {@link IFlowRenderer}).
 */
public interface FlowRendererProviderExtension extends SingularExtension, Serializable {

    public static final String FOR_USER_DISPLAY = "ForUserDisplay";

    /** Instantiates the renders implementation. */
    @Nonnull
    public IFlowRenderer getRenderer();

}