package org.opensingular.lib.wicket.views;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.opensingular.lib.commons.views.ViewGenerator;
import org.opensingular.lib.commons.views.ViewMultiGenerator;
import org.opensingular.lib.commons.views.ViewOutput;

import javax.annotation.Nonnull;

public class ViewOutputHtmlWebComponent extends WebComponent {
    private final ISupplier<ViewGenerator> viewGeneratorSupplier;

    public ViewOutputHtmlWebComponent(String id, ISupplier<ViewGenerator> viewGeneratorSupplier) {
        super(id);
        this.viewGeneratorSupplier = viewGeneratorSupplier;
    }

    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        ViewOutputWriterHtmlFromWicket viewOutput = new ViewOutputWriterHtmlFromWicket(getRequestCycle());
        ViewGenerator generator = viewGeneratorSupplier.get();
        generator = resolveGenerator(generator, viewOutput);
        generator.generateView(viewOutput);
    }

    private static ViewGenerator resolveGenerator(@Nonnull ViewGenerator generator, ViewOutput<java.io.Writer> vOut) {
        if (generator instanceof ViewMultiGenerator) {
            return ((ViewMultiGenerator) generator).getGeneratorFor(vOut);
        }
        return generator;
    }
}
