package org.opensingular.flow.core;

import org.junit.Before;
import org.junit.Test;
import org.opensingular.flow.core.entity.IEntityProcessVersion;
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
    public void inexistentProcessVersionShouldReturnNull() throws Exception {
        IEntityProcessVersion entityProcessVersion = mbpmBean.getPersistenceService().retrieveProcessVersionByCod(0);

        assertNull(entityProcessVersion);
    }
}