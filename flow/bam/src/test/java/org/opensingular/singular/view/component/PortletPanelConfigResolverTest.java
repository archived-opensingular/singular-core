package org.opensingular.singular.view.component;

import com.opensingular.bam.wicket.view.component.AmChartViewResultPanel;
import com.opensingular.bam.wicket.view.component.ViewResultPanel;
import org.apache.wicket.util.tester.WicketTester;

import com.opensingular.bam.client.portlet.AmChartPortletConfig;
import com.opensingular.bam.client.portlet.PortletContext;
import static com.opensingular.bam.wicket.view.component.PortletViewConfigResolver.newViewResult;
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