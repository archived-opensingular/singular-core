/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.custom;

import static org.opensingular.singular.util.wicket.util.WicketUtils.*;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.media.video.Video;
import org.apache.wicket.validation.validator.UrlValidator;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.singular.form.wicket.IWicketComponentMapper;
import org.opensingular.singular.form.wicket.WicketBuildContext;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSControls;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSFormGroup;
import org.opensingular.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;
import org.opensingular.singular.util.wicket.model.IMappingModel;

/**
 * Custom String Mapper
 */
@CaseItem(componentName = "Custom Mapper", subCaseName = "Vídeo", group = Group.CUSTOM)
public class CaseCustomVideoMapperPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<SIComposite> tipoMyForm = pb.createCompositeType("testForm");

        tipoMyForm.addFieldString("video")
            .addInstanceValidator(v -> {
                if (!new UrlValidator().isValid(v.getInstance().getValue())) {
                    v.error("URL inválida");
                }
            })
            //@destacar
            .withCustomMapper(() -> new VideoMapper())
            .asAtr().label("Vídeo").required(true);
    }

    private static final class VideoMapper implements IWicketComponentMapper {
        @Override
        public void buildView(WicketBuildContext ctx) {
            final IMappingModel<String> labelModel = IMappingModel.of(ctx.getModel()).map(it -> it.asAtr().getLabel());
            switch (ctx.getViewMode()) {
                case EDIT:
                    ctx.getContainer()
                        .appendComponent(id -> new BSFormGroup(id, BSGridSize.MD)
                            .appendControls(12, controlsId -> new BSControls(controlsId)
                                .appendLabel(new Label("label", labelModel))
                                .appendInputText(ctx.configure(this, new TextField<>("url", ctx.getValueModel())))
                                .appendFeedback(ctx.createFeedbackCompactPanel("feedback"))
                                .appendHelpBlock($m.ofValue("Exemplos: "
                                    + "<ul>"
                                    + " <li>http://techslides.com/demos/sample-videos/small.mp4</li>"
                                    + " <li>http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_1mb.mp4</li>"
                                    + "</ul>"), false)));
                    break;
                case READ_ONLY:
                    Video video = new Video("video", ctx.getModel().getObject().getValueWithDefault(String.class));
                    video.setWidth(320);
                    ctx.getContainer().appendTag("video", video);
                    break;
            }
        }
    }
}
