package br.net.mirante.singular.test;

import br.net.mirante.singular.DefinicaoComVariaveis;
import br.net.mirante.singular.definicao.InstanciaDefinicaoComVariavel;
import br.net.mirante.singular.flow.core.MBPM;
import br.net.mirante.singular.persistence.entity.ExecutionVariable;
import br.net.mirante.singular.persistence.entity.ProcessInstance;
import br.net.mirante.singular.persistence.entity.Variable;
import br.net.mirante.singular.persistence.entity.VariableType;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InstanciaDefinicaoComVariavelTest extends TestSupport {

    public static   String pessoa = "Pessoa X";
    public static   BigDecimal qualquerCoisa = new BigDecimal("1111111123242343240.00001E-3");

    @Before
    public void setup() {
        assertNotNull(mbpmBean);
        MBPM.setConf(mbpmBean);

    }

    @Test
    public void teste1UsoDeVariaveis() {
        InstanciaDefinicaoComVariavel id2 = new InstanciaDefinicaoComVariavel();
        id2.start();

        String nome = id2.getValorVariavel("nome");
        BigDecimal qualquerCoisa = id2.getValorVariavel("qualquerCoisa");

        assertEquals(nome, DefinicaoComVariaveis.STRING_USADA_NO_TESTE);
        assertEquals(qualquerCoisa, DefinicaoComVariaveis.BIGDECIMAL_USADO_NO_TESTE);


    }

    @Test()
    public void teste2PersistenciaVariaveis(){
        DefinicaoComVariaveis d = mbpmBean.getProcessDefinition(DefinicaoComVariaveis.class);
        List<ProcessInstance> instances = testDAO.findAllProcessInstancesByDefinition(d.getEntity());
        for (ProcessInstance p : instances){
            List<Variable> variables = testDAO.retrieveVariablesByInstance(p.getCod());
            assertEquals(variables.size(), 2);
            List<ExecutionVariable> executionVariables = testDAO.retrieveExecutionVariablesByInstance(p.getCod());
            assertEquals(variables.size(),2);
            List<VariableType> variableTypes = testDAO.retrieveVariablesTypesByInstance(p.getCod());
            assertEquals(variables.size(),2);
        }
    }



}
