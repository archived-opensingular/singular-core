package br.net.mirante.singular.flow.core;

import br.net.mirante.singular.flow.util.vars.VarInstanceMap;

public class ExecucaoMTask {

    private ProcessInstance instanciaProcesso;

    private TaskInstance instanciaTarefa;

    private String transicaoResultado;

    private VarInstanceMap<?> input;

    @Deprecated
    public ExecucaoMTask(ProcessInstance instanciaProcesso, VarInstanceMap<?> input) {
        this(instanciaProcesso, null, input);
    }

    public ExecucaoMTask(ProcessInstance instanciaProcesso, TaskInstance instanciaTarefa, VarInstanceMap<?> input) {
        this.instanciaProcesso = instanciaProcesso;
        this.instanciaTarefa = instanciaTarefa;
        this.input = input;
    }

    public ExecucaoMTask(TaskInstance instanciaTarefa, VarInstanceMap<?> input) {
        this(null, instanciaTarefa, input);
    }

    public ProcessInstance getInstanciaProcesso() {
        if (instanciaProcesso == null) {
            instanciaProcesso = instanciaTarefa.getProcessInstance();
        }
        return instanciaProcesso;
    }

    public TaskInstance getInstanciaTarefa() {
        if (instanciaTarefa == null) {
            return instanciaProcesso.getTarefaAtual();
        }
        return instanciaTarefa;
    }

    public String getTransicaoResultado() {
        return transicaoResultado;
    }

    public void setTransicaoResultado(String transicaoResultado) {
        this.transicaoResultado = transicaoResultado;
    }

    public VarInstanceMap<?> getInput() {
        if (input == null) {
            input = VarInstanceMap.empty();
        }
        return input;
    }

}
