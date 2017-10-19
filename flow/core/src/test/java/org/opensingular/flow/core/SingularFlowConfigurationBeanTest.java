package org.opensingular.flow.core;

import org.junit.Before;
import org.junit.Test;
import org.opensingular.flow.core.entity.IEntityFlowVersion;
import org.opensingular.flow.test.support.TestFlowSupport;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class SingularFlowConfigurationBeanTest extends TestFlowSupport {

    @Before
    public void setUp() {
        assertNotNull(mbpmBean);
        Flow.setConf(mbpmBean, true);
    }


    @Test
    public void inexistentFlowVersionShouldReturnNull() throws Exception {
        IEntityFlowVersion entityFlowVersion = mbpmBean.getPersistenceService().retrieveFlowVersionByCod(0);

        assertNull(entityFlowVersion);
    }
}