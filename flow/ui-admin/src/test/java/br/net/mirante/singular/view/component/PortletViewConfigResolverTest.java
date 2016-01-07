package br.net.mirante.singular.view.component;

import org.apache.wicket.util.tester.WicketTester;

import br.net.mirante.singular.bamclient.portlet.AmChartPortletConfig;
import junit.framework.TestCase;

/**
 * Created by danilo.mesquita on 07/01/2016.
 */
public class PortletViewConfigResolverTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        WicketTester wt = new WicketTester();
    }

    public void testResolve() throws Exception {
        ViewResult viewResult = PortletViewConfigResolver.newViewResult("test", new AmChartPortletConfig());
        assertTrue(viewResult instanceof AmChartViewResult);
    }
}