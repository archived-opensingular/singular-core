package br.net.mirante.singular;

import br.net.mirante.singular.flow.core.ExecucaoMTask;
import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.ITaskPageStrategy;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskAccessStrategy;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.builder.BEnd;
import br.net.mirante.singular.flow.core.builder.BJava;
import br.net.mirante.singular.flow.core.builder.BPeople;
import br.net.mirante.singular.flow.core.builder.BTask;
import br.net.mirante.singular.flow.core.builder.FlowBuilder;
import br.net.mirante.singular.flow.core.builder.FlowBuilderImpl;
import br.net.mirante.singular.flow.util.view.WebRef;
import br.net.mirante.singular.persistence.entity.Actor;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Definicao extends ProcessDefinition<InstanciaDefinicao> {

    public Definicao() {
        super(InstanciaDefinicao.class);
    }

    @Override
    protected FlowMap createFlowMap() {
        setName("Teste", "Definicao");

        FlowMap flowMap = new FlowMap(this);

        flowMap.addJavaTask(() -> "Aprovar Solicitação");


        FlowBuilder flow = new FlowBuilderImpl(this);


        BPeople task = flow.addPeopleTask(() -> "Aprovar Solicitação", new EstrategiaAcessoLiberado());
        task.withExecutionPage(new A());

        flow.setStartTask(task);
        BEnd end = flow.addEnd(() -> "Aprovado");
        flow.addTransition(task, end);

        return flow.build();
    }

    private static final class EstrategiaAcessoLiberado extends TaskAccessStrategy<InstanciaDefinicao> {


        @Override
        public boolean canExecute(InstanciaDefinicao instance, MUser user) {
            return true;
        }

        @Override
        public Set<Serializable> getFirstLevelUsersCodWithAccess(InstanciaDefinicao instancia) {
            return Collections.emptySet();
        }

        @Override
        public List<? extends MUser> listAllocableUsers(InstanciaDefinicao instancia) {
            return Collections.singletonList(CoisasQueDeviamSerParametrizadas.USER);
        }

        @Override
        public List<String> getExecuteRoleNames(ProcessDefinition<?> definicao, MTask<?> task) {
            return Collections.singletonList("Teste");
        }
    }

    private static final class A implements ITaskPageStrategy {

        @Override
        public WebRef getPageFor(TaskInstance taskInstance, MUser user) {
            return null;
        }
    }

}
