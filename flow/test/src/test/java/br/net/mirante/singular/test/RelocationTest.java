package br.net.mirante.singular.test;

import br.net.mirante.singular.commons.base.SingularProperties;
import br.net.mirante.singular.flow.core.*;
import br.net.mirante.singular.flow.core.builder.FlowBuilderImpl;
import br.net.mirante.singular.flow.core.builder.ITaskDefinition;
import br.net.mirante.singular.flow.core.defaults.NullTaskAccessStrategy;
import br.net.mirante.singular.flow.core.ws.SingularWS;
import br.net.mirante.singular.test.support.TestSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Created by nuk on 15/04/16.
 */
@ActiveProfiles("mssql")
public class RelocationTest extends TestSupport {

    @BeforeClass
    public static void configProperites() {
        SingularProperties.INSTANCE.loadFrom(ClassLoader.getSystemClassLoader().getResourceAsStream("singular-mssql.properties"));
    }

    @Before
    public void setup() {
        Flow.setConf(mbpmBean, true);
    }

    @After
    public void tearDown() {
        ProcessDefinitionCache.invalidateAll();
    }

    @Test public void relocatesTaskUser(){
        P p = new P();
        ProcessInstance id = p.newInstance();
        id.start();


        assertThat(id.getCurrentTask().getAllocatedUser()).isNull();

        new SingularWS().relocateTask(p.getKey(), id.getEntityCod().longValue(), "1");
        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(testDAO.getSomeUser(1));

        new SingularWS().relocateTask(p.getKey(), id.getEntityCod().longValue(), "2");
        assertThat(id.getCurrentTask().getAllocatedUser()).isEqualTo(testDAO.getSomeUser(2));
    }


    public static class P extends ProcessDefinition<ProcessInstance> {

        public enum PTask implements ITaskDefinition {
            START, DO1, DO2, END;

            @Override
            public String getName() {
                return this.name();
            }
        }

        public P() {
            super("P-P",ProcessInstance.class);
        }

        @Override
        protected FlowMap createFlowMap() {
            setName("TT", "PP");

            FlowBuilderImpl flow = new FlowBuilderImpl(this);
            flow.addJavaTask(PTask.START).call(this::doSomething);
            flow.addPeopleTask(PTask.DO1, new NullTaskAccessStrategy());
//        flow.addJavaTask(PTask.DO2).call(this::doSomething);

            flow.addEnd(PTask.END);
            flow.setStartTask(PTask.START);

            flow.from(PTask.START).go(PTask.DO1);
//        flow.from(PTask.START).go(PTask.DO2);
            flow.from(PTask.DO1).go(PTask.END);
//        flow.from(PTask.DO2).go(PTask.END);

            return flow.build();
        }

        public void doSomething(ProcessInstance instancia, ExecutionContext ctxExecucao) {

        }

    }
}

