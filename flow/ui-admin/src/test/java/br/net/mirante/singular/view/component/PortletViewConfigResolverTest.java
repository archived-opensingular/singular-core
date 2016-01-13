package br.net.mirante.singular.view.component;

import org.apache.wicket.util.tester.WicketTester;

import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;
import static br.net.mirante.singular.view.component.PortletViewConfigResolver.newViewResult;
import junit.framework.TestCase;
import static org.apache.wicket.model.Model.of;

public class PortletViewConfigResolverTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        WicketTester wt = new WicketTester();
    }

    public void testResolve() throws Exception {
        ViewResult viewResult = newViewResult("test", of(new AmChartPortletConfig(null, null)), of(new PortletContext()));
        assertTrue(viewResult instanceof AmChartViewResult);
    }
}