package org.opensingular.lib.wicket.util.bootstrap.layout;

import org.apache.wicket.Component;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.wicket.util.SingleFormDummyPage;
import org.opensingular.lib.wicket.util.WicketUtilsDummyApplication;

public class IBSGridColTest {

    @Test
    public void test() {
        WicketTester tester = new WicketTester(new WicketUtilsDummyApplication());

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                return new TemplatePanel(contentId, ""
                    + "\n<span wicket:id='xs'></span>"
                    + "\n<span wicket:id='sm'></span>"
                    + "\n<span wicket:id='md'></span>"
                    + "\n<span wicket:id='lg'></span>"
                    + "\n")
                        .add(new BSLabel("xs").xs(1).xsHidden(true).xsOffset(1))
                        .add(new BSLabel("sm").sm(2).smHidden(true).smOffset(2))
                        .add(new BSLabel("md").md(3).mdHidden(true).mdOffset(3))
                        .add(new BSLabel("lg").lg(4).lgHidden(true).lgOffset(4));
            }
        });

        BSLabel xs = (BSLabel) tester.getComponentFromLastRenderedPage(SingleFormDummyPage.PARENT_PATH + "xs");
        Assert.assertEquals(1, xs.xs());
        Assert.assertTrue(xs.xsHidden());
        Assert.assertEquals(1, xs.xsOffset());

        BSLabel sm = (BSLabel) tester.getComponentFromLastRenderedPage(SingleFormDummyPage.PARENT_PATH + "sm");
        Assert.assertEquals(2, sm.sm());
        Assert.assertTrue(sm.smHidden());
        Assert.assertEquals(2, sm.smOffset());

        BSLabel md = (BSLabel) tester.getComponentFromLastRenderedPage(SingleFormDummyPage.PARENT_PATH + "md");
        Assert.assertEquals(3, md.md());
        Assert.assertTrue(md.mdHidden());
        Assert.assertEquals(3, md.mdOffset());

        BSLabel lg = (BSLabel) tester.getComponentFromLastRenderedPage(SingleFormDummyPage.PARENT_PATH + "lg");
        Assert.assertEquals(4, lg.lg());
        Assert.assertTrue(lg.lgHidden());
        Assert.assertEquals(4, lg.lgOffset());
    }

}
