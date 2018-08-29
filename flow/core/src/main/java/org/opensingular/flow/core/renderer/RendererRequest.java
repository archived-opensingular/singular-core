package org.opensingular.flow.core.renderer;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.opensingular.flow.core.FlowDefinition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Represents a configuration for a flow  diagram rendering.
 *
 * @author Daniel C. Bordin
 * @since 2018-08-19
 */
public class RendererRequest {

    private final IFlowRenderer renderer;
    @Nonnull
    private final FlowDefinition<?> definition;

    private ExecutionHistoryForRendering instanceHistory;

    private boolean suppressTaskIcons = false;

    RendererRequest(@Nonnull IFlowRenderer renderer, @Nonnull FlowDefinition<?> definition) {
        this.renderer = renderer;
        this.definition = definition;
    }

    /** Indicates if the diagram should have a icon in each task identifying the type of the task. */
    public boolean isSuppressTaskIcons() {
        return suppressTaskIcons;
    }

    /** Indicates if the diagram should have a icon in each task identifying the type of the task. */
    @Nonnull
    public RendererRequest setSuppressTaskIcons(boolean suppressTaskIcons) {
        this.suppressTaskIcons = suppressTaskIcons;
        return this;
    }

    /** Generates a byte array with PNG image representing the flow. */
    @Nonnull
    public byte[] generatePng() {
        return renderer.generatePng(this);
    }

    /** Generates a PNG image representing the flow to the output stream provided. */
    public void generatePng(@Nonnull OutputStream out) throws IOException {
        renderer.generatePng(this, out);
    }

    /** The renderer responsible to actually generate the diagrams. */
    @Nonnull
    public IFlowRenderer getRenderer() {
        return renderer;
    }

    /** The flow that will be render. */
    @Nonnull
    public FlowDefinition<?> getDefinition() {
        return definition;
    }

    /** Set the flow's history info to be added to the generated diagram. */
    @Nonnull
    public RendererRequest setInstanceHistory(@Nullable ExecutionHistoryForRendering instanceHistory) {
        this.instanceHistory = instanceHistory;
        return this;
    }

    /** The flow's history info to be added to the generated diagram. */
    @Nullable
    public ExecutionHistoryForRendering getInstanceHistory() {
        return instanceHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RendererRequest that = (RendererRequest) o;
        return Objects.equals(definition, that.definition) && Objects.equals(instanceHistory, that.instanceHistory);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(definition).append(instanceHistory).toHashCode();
    }
}
