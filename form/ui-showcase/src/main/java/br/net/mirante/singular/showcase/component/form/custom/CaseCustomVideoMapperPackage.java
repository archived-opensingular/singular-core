/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.custom;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.*;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.media.video.Video;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSFormGroup;
import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;
import br.net.mirante.singular.util.wicket.model.IMappingModel;

/**
 * Custom String Mapper
 */
@CaseItem(componentName = "Custom Mapper", subCaseName = "Vídeo", group = Group.CUSTOM)
public class CaseCustomVideoMapperPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<SIComposite> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldString("video")
            //@destacar
            .withCustomMapper(() -> new VideoMapper())
            .asAtr().label("Vídeo");

    }

    private static final class VideoMapper implements IWicketComponentMapper {
        @Override
        public void buildView(WicketBuildContext ctx) {
            final IMappingModel<String> labelModel = IMappingModel.of(ctx.getModel()).map(it -> it.asAtr().getLabel());
            switch (ctx.getViewMode()) {
                case EDITION:
                    ctx.getContainer()
                        .appendComponent(id -> new BSFormGroup(id, BSGridSize.MD)
                            .appendControls(12, controlsId -> new BSControls(controlsId)
                                .appendLabel(new Label("label", labelModel))
                                .appendInputText(new TextField<>("url", ctx.getValueModel()))
                                .appendFeedback(ctx.createFeedbackPanel("feedback"))
                                .appendHelpBlock($m.ofValue("Exemplos: "
                                    + "<ul>"
                                    + " <li>http://techslides.com/demos/sample-videos/small.mp4</li>"
                                    + " <li>http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4</li>"
                                    + "</ul>"), false)));
                    break;
                case VISUALIZATION:
                    Video video = new Video("video", ctx.getModel().getObject().getValueWithDefault(String.class));
                    video.setWidth(320);
                    ctx.getContainer().appendTag("video", video);
                    break;
            }
        }
    }
}
