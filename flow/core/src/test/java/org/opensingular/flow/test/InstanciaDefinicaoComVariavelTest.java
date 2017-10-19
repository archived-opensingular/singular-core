package org.opensingular.flow.test;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.persistence.entity.ExecutionVariableEntity;
import org.opensingular.flow.persistence.entity.FlowInstanceEntity;
import org.opensingular.flow.persistence.entity.VariableInstanceEntity;
import org.opensingular.flow.persistence.entity.VariableTypeInstance;
import org.opensingular.flow.test.definicao.DefinicaoComVariaveis;
import org.opensingular.flow.test.support.TestFlowSupport;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InstanciaDefinicaoComVariavelTest extends TestFlowSupport {

    @Before
    public void setUp() {
        assertNotNull(mbpmBean);
        Flow.setConf(mbpmBean, true);
    }

    @Test
    public void teste1UsoDeVariaveis() {
        FlowInstance pi = new DefinicaoComVariaveis().prepareStartCall().createAndStart();
        String nome = pi.getVariableValue("nome");
        BigDecimal qualquerCoisa = pi.getVariableValue("qualquerCoisa");
        assertEquals(nome, DefinicaoComVariaveis.STRING_USADA_NO_TESTE);
        assertEquals(qualquerCoisa, DefinicaoComVariaveis.BIGDECIMAL_USADO_NO_TESTE);
    }

    @Test()
    public void teste2PersistenciaVariaveis() {
        DefinicaoComVariaveis d = mbpmBean.getFlowDefinition(DefinicaoComVariaveis.class);
        List<FlowInstanceEntity> instances = testDAO.findAllFlowInstancesByDefinition(d.getEntityFlowVersion());
        for (FlowInstanceEntity p : instances) {
            List<VariableInstanceEntity> variables = testDAO.retrieveVariablesByInstance(p.getCod());
            assertEquals(2, variables.size());
            List<ExecutionVariableEntity> executionVariables = testDAO.retrieveExecutionVariablesByInstance(p.getCod());
            assertEquals(0, executionVariables.size());
            List<VariableTypeInstance> variableTypes = testDAO.retrieveVariablesTypesByInstance(p.getCod());
            assertEquals(2, variableTypes.size());
        }
    }
}
