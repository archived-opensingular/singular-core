package org.opensingular.form.flatview.mapper;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.flatview.AbstractFlatViewGenerator;
import org.opensingular.form.flatview.FlatViewContext;
import org.opensingular.form.flatview.FlatViewGenerator;
import org.opensingular.form.view.Block;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.ViewResolver;
import org.opensingular.lib.commons.canvas.DocumentCanvas;

public class BlockFlatViewGenerator extends AbstractFlatViewGenerator {
    @Override
    protected void doWriteOnCanvas(DocumentCanvas canvas, FlatViewContext context) {
        SIComposite instance = (SIComposite) context.getInstance();
        SViewByBlock viewByBlock = (SViewByBlock) ViewResolver.resolveView(instance.getType());

        for (Block block : viewByBlock.getBlocks()) {
            String blockTitle = null;
            boolean hideTitle = false;

            if (StringUtils.isNotEmpty(block.getName())) {
                blockTitle = block.getName();
            } else if (block.getTypes().size() == 1) {
                blockTitle = instance.getField(block.getTypes().get(0)).asAtr().getLabel();
            }

            if (block.getTypes().size() == 1) {
                hideTitle = true;
            }
            canvas.addSubtitle(blockTitle);
            DocumentCanvas subcanvas = canvas.addChild();

            for (String type : block.getTypes()) {
                SInstance f = instance.getField(type);
                FlatViewGenerator flatViewGenerator = f.getAspect(FlatViewGenerator.ASPECT_FLAT_VIEW_GENERATOR).orElse(null);

                if (flatViewGenerator != null) {
                    flatViewGenerator.writeOnCanvas(subcanvas, new FlatViewContext(f, hideTitle));
                }
            }
        }
    }
}