package org.opensingular.lib.wicket.util.bootstrap.layout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.opensingular.lib.wicket.util.SingleFormDummyPage;
import org.opensingular.lib.wicket.util.WicketUtilsDummyApplication;
import org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol.BSGridSize;
import org.opensingular.lib.wicket.util.feedback.BSFeedbackPanel;

public class BSFormHorizontalTest extends Object {

    @Test
    public void test() {
        WicketTester tester = new WicketTester(new WicketUtilsDummyApplication());

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                new BSFormHorizontal("ignored", valueModel());
                BSFormHorizontal content = new BSFormHorizontal(contentId)
                    .setDefaultGridSize(BSGridSize.MD);
                content
                    .appendGroupLabelControlsFeedback(1, "", 1, new BSFeedbackPanel("feedback"), BSControls::new)
                    .appendGroupLabelControlsFeedback(1, new Label("label"), "", 1, new BSFeedbackPanel("feedback"), BSControls::new)
                    .appendGroup(BSFormGroup::new)
                    .appendGroupLabelControls(1, "", 1, BSControls::new)
                    .appendGroupOffsetControls(1, 1, BSControls::new)
                    .newControlsInGroup(1, 1);
                content
                    .newControlsInGroup(1, "", 1);
                content
                    .newGroup()
                    .newControls("controls");

                content
                    .newGroup()
                    .appendLabel(1, valueModel())
                    .newSubgroup()
                    .appendControls(1, BSControls::new)
                    .appendSubgroup()
                    .appendSubgroupLabelControls(1, new Label("label"), "", 1, BSControls::new)
                    .appendSubgroupLabelControlsFeedback(1, new Label("label"), "", 1, BSControls::new)
                    .newControls(1);
                return content;
            }
        });

        tester.submitForm(SingleFormDummyPage.FORM_ID);
        tester.assertNoErrorMessage();
    }

    protected static Model<Serializable> valueModel() {
        return Model.of();
    }

    protected static IModel<List<Serializable>> listModel() {
        return Model.ofList(new ArrayList<>());
    }
}
