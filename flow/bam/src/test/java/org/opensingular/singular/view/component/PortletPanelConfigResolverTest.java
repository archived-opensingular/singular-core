package org.opensingular.singular.view.component;

import org.opensingular.singular.bam.wicket.view.component.AmChartViewResultPanel;
import org.opensingular.singular.bam.wicket.view.component.ViewResultPanel;
import org.apache.wicket.util.tester.WicketTester;

import org.opensingular.singular.bamclient.portlet.AmChartPortletConfig;
import org.opensingular.singular.bamclient.portlet.PortletContext;
import static org.opensingular.singular.bam.wicket.view.component.PortletViewConfigResolver.newViewResult;
import junit.framework.TestCase;
import static org.apache.wicket.model.Model.of;

public class PortletPanelConfigResolverTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        WicketTester wt = new WicketTester();
    }

    public void testResolve() throws Exception {
        ViewResultPanel viewResultPanel = newViewResult("test", of(new AmChartPortletConfig(null, null)), of(new PortletContext()));
        assertTrue(viewResultPanel instanceof AmChartViewResultPanel);
    }
}