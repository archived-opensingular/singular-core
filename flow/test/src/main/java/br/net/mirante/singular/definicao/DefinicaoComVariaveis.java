package br.net.mirante.singular.definicao;

import br.net.mirante.singular.flow.core.ExecutionContext;
import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.builder.BEnd;
import br.net.mirante.singular.flow.core.builder.BJava;
import br.net.mirante.singular.flow.core.builder.FlowBuilder;
import br.net.mirante.singular.flow.core.builder.FlowBuilderImpl;
import br.net.mirante.singular.flow.core.builder.ITaskDefinition;
import br.net.mirante.singular.flow.util.vars.VarDefinitionImpl;
import br.net.mirante.singular.flow.util.vars.types.VarTypeDecimal;
import br.net.mirante.singular.flow.util.vars.types.VarTypeString;

import java.math.BigDecimal;

public class DefinicaoComVariaveis extends ProcessDefinition<ProcessInstance> {

    public static final BigDecimal BIGDECIMAL_USADO_NO_TESTE = new BigDecimal("1111111123242343240.00001E-3");
    public static final String STRING_USADA_NO_TESTE = "Pessoa X";

    public DefinicaoComVariaveis() {
        super("DefVar",ProcessInstance.class);
        getVariables().addVariable(new VarDefinitionImpl("nome", "Nome de Alguém", new VarTypeString(), false));
        getVariables().addVariable(new VarDefinitionImpl("qualquerCoisa", "Qualquer Coisa Numerica", new VarTypeDecimal(), false));
    }

    @Override
    protected FlowMap createFlowMap() {
        FlowBuilder f = new FlowBuilderImpl(this);

        ITaskDefinition PRINT = () -> "Print Variavel";
        f.addJavaTask(PRINT).call(this::printVar);

        ITaskDefinition SET_VARIAVEL = () -> "Definir Variavel";
        f.addJavaTask(SET_VARIAVEL).call(this::setVar);

        ITaskDefinition APROVAR = () -> "Aprovar Definiçâo";
        f.addJavaTask(APROVAR).call(this::print);

        ITaskDefinition END = () -> "Aprovado";
        f.addEnd(END);

        f.setStartTask(SET_VARIAVEL);
        f.from(SET_VARIAVEL).go(APROVAR);
        f.from(APROVAR).go(PRINT);
        f.from(PRINT).go(END);

        return f.build();
    }

    public ProcessInstance start() {
        ProcessInstance instancia = newInstance();
        instancia.start();
        return instancia;
    }

    public void print(ProcessInstance instancia, ExecutionContext ctxExecucao) {
        System.out.println("legal");
    }

    public void setVar(ProcessInstance instancia, ExecutionContext ctxExecucao) {
        instancia.setVariavel("nome", STRING_USADA_NO_TESTE);
        instancia.setVariavel("qualquerCoisa", BIGDECIMAL_USADO_NO_TESTE);

        instancia.saveEntity();
    }

    public void printVar(ProcessInstance instancia, ExecutionContext ctxExecucao) {
        System.out.println("########### nome          #####>" + instancia.getValorVariavel("nome"));
        System.out.println("########### qualquerCoisa #####>" + instancia.getValorVariavel("qualquerCoisa"));
    }
}
