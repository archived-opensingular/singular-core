package br.net.mirante.singular;

import br.net.mirante.singular.definicao.InstanciaPeticao;
import br.net.mirante.singular.flow.core.ExecucaoMTask;
import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.builder.BEnd;
import br.net.mirante.singular.flow.core.builder.BJava;
import br.net.mirante.singular.flow.core.builder.FlowBuilder;
import br.net.mirante.singular.flow.core.builder.FlowBuilderImpl;
import br.net.mirante.singular.flow.util.vars.VarDefinitionImpl;
import br.net.mirante.singular.flow.util.vars.types.VarTypeDecimal;
import br.net.mirante.singular.flow.util.vars.types.VarTypeString;

import java.math.BigDecimal;

public class DefinicaoComVariaveis extends ProcessDefinition<InstanciaPeticao> {

    public static final BigDecimal BIGDECIMAL_USADO_NO_TESTE =  new BigDecimal("1111111123242343240.00001E-3");
    public static final String STRING_USADA_NO_TESTE = "Pessoa X";

    public DefinicaoComVariaveis() {
        super(InstanciaPeticao.class);
        getVariables().addVariable(new VarDefinitionImpl("nome", "Nome de Alguém", new VarTypeString(), false));
        getVariables().addVariable(new VarDefinitionImpl("qualquerCoisa", "Qualquer Coisa Numerica", new VarTypeDecimal(), false));
    }


    @Override
    protected FlowMap createFlowMap() {
        FlowBuilder f = new FlowBuilderImpl(this);

        BJava PRINT = f.addJavaTask(() -> "Print Variavel");
        PRINT.call(this::printVar);

        BJava SET_VARIAVEL = f.addJavaTask(() -> "Definir Variavel");
        SET_VARIAVEL.call(this::setVar);

        BJava APROVAR = f.addJavaTask(() -> "Aprovar Definiçâo");
        APROVAR.call(this::print);

        BEnd END = f.addEnd(() -> "Aprovado");

        f.setStartTask(SET_VARIAVEL);
        f.addTransition(SET_VARIAVEL, APROVAR);
        f.addTransition(APROVAR, PRINT);
        f.addTransition(PRINT, END);

        return f.build();
    }


    public void print(ProcessInstance instancia, ExecucaoMTask ctxExecucao) {
        System.out.println("legal");
    }

    public void setVar(ProcessInstance instancia, ExecucaoMTask ctxExecucao) {
//        instancia.setVariavel("nome2", "Pessoa Y");
        instancia.setVariavel("nome", STRING_USADA_NO_TESTE);
        instancia.setVariavel("qualquerCoisa", BIGDECIMAL_USADO_NO_TESTE);

        instancia.saveEntity();
    }

    public void printVar(ProcessInstance instancia, ExecucaoMTask ctxExecucao) {
//        System.out.println("########### nome2         #####>" + instancia.getValorVariavel("nome2"));
        System.out.println("########### nome          #####>" + instancia.getValorVariavel("nome"));
        System.out.println("########### qualquerCoisa #####>" + instancia.getValorVariavel("qualquerCoisa"));

    }

}
