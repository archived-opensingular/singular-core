package br.net.mirante.singular.test;

import java.math.BigDecimal;
import java.util.List;

import br.net.mirante.singular.definicao.DefinicaoComVariaveis;
import br.net.mirante.singular.flow.core.ProcessInstance;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.persistence.entity.ExecutionVariable;
import br.net.mirante.singular.persistence.entity.Variable;
import br.net.mirante.singular.persistence.entity.VariableType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InstanciaDefinicaoComVariavelTest extends TestSupport {

    @Before
    public void setup() {
        assertNotNull(mbpmBean);
        Flow.setConf(mbpmBean);
    }

    @Test
    public void teste1UsoDeVariaveis() {
        ProcessInstance pi = new DefinicaoComVariaveis().newInstance();
        pi.start();
        String nome = pi.getValorVariavel("nome");
        BigDecimal qualquerCoisa = pi.getValorVariavel("qualquerCoisa");
        assertEquals(nome, DefinicaoComVariaveis.STRING_USADA_NO_TESTE);
        assertEquals(qualquerCoisa, DefinicaoComVariaveis.BIGDECIMAL_USADO_NO_TESTE);
    }

    @Test()
    public void teste2PersistenciaVariaveis() {
        DefinicaoComVariaveis d = mbpmBean.getProcessDefinition(DefinicaoComVariaveis.class);
        List<br.net.mirante.singular.persistence.entity.ProcessInstance> instances = testDAO.findAllProcessInstancesByDefinition(d.getEntityProcessVersion());
        for (br.net.mirante.singular.persistence.entity.ProcessInstance p : instances) {
            List<Variable> variables = testDAO.retrieveVariablesByInstance(p.getCod());
            assertEquals(2, variables.size());
            List<ExecutionVariable> executionVariables = testDAO.retrieveExecutionVariablesByInstance(p.getCod());
            assertEquals(0, executionVariables.size());
            List<VariableType> variableTypes = testDAO.retrieveVariablesTypesByInstance(p.getCod());
            assertEquals(2, variableTypes.size());
        }
    }
}
