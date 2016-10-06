package org.opensingular.singular.test;

import static org.fest.assertions.api.Assertions.*;

import javax.inject.Inject;

import org.opensingular.flow.core.DefinitionInfo;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.view.GraphComponent;

import org.opensingular.singular.commons.base.SingularPropertiesImpl;
import org.opensingular.flow.core.ExecutionContext;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.FlowMap;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.flow.core.builder.FlowBuilderImpl;
import org.opensingular.flow.core.builder.ITaskDefinition;
import org.opensingular.flow.core.defaults.NullTaskAccessStrategy;
import org.opensingular.flow.core.renderer.YFilesFlowConverter;
import org.opensingular.flow.test.TestDAO;
import org.opensingular.flow.persistence.util.HibernateSingularFlowConfigurationBean;

@ActiveProfiles("mssql")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class YFilesFlowConverterTest {

    @Inject
    protected HibernateSingularFlowConfigurationBean mbpmBean;

    @Inject
    protected TestDAO testDAO;

    @Inject
    protected SessionFactory sessionFactory;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    static private ProcessInstance id;
    private Session session;

    @BeforeClass
    public static void configProperties() {
        SingularPropertiesImpl.get().reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource("singular-mssql.properties"));
    }

    static boolean started;
    @Before
    public void setup() {
        if(!started){
            Flow.setConf(mbpmBean, true);

            session = sessionFactory.openSession();
            mbpmBean.setSessionLocator(() -> session);
            session.beginTransaction();

            P p = new P();
            id = p.newInstance();
            id.start();
        }
        started = true;
    }

    @Test public void createNodesForEachTask(){
        GraphComponent graphComponent = new YFilesFlowConverter(id.getProcessDefinition()).build().toGraphComponent();

        IGraph graph = graphComponent.getGraph();
        assertThat(graph.getNodes())
                .hasSize(3);
        assertThat(extractProperty("text").from(graph.getNodeLabels()))
                .containsOnly("START","DO1","END");
    }

    @Test public void createEdgesForEachTransition(){
        GraphComponent graphComponent = new YFilesFlowConverter(id.getProcessDefinition()).build().toGraphComponent();

        IGraph graph = graphComponent.getGraph();
        assertThat(graph.getNodes())
                .hasSize(3);
        assertThat(extractProperty("text").from(graph.getEdgeLabels()))
                .containsOnly("Go To 1","Finish From 1");
    }

    @DefinitionInfo("P-Y")
    public static class P extends ProcessDefinition<ProcessInstance> {

        public enum PTask implements ITaskDefinition {
            START, DO1, DO2, END;

            @Override
            public String getName() {
                return this.name();
            }
        }

        public P() {
            super(ProcessInstance.class);
        }

        @Override
        protected FlowMap createFlowMap() {
            setName("TTFY", "TesteFlowY");

            FlowBuilderImpl flow = new FlowBuilderImpl(this);
            flow.addJavaTask(PTask.START).call(this::doSomething);
            flow.addPeopleTask(PTask.DO1, new NullTaskAccessStrategy());
//
            flow.addEnd(PTask.END);
            flow.setStartTask(PTask.START);
//
            flow.from(PTask.START).go("Go To 1",PTask.DO1);
//        flow.from(PTask.START).go(PTask.DO2);
            flow.from(PTask.DO1).go("Finish From 1",PTask.END);
//        flow.from(PTask.DO2).go(PTask.END);

            return flow.build();
        }

        public void doSomething(ProcessInstance instancia, ExecutionContext ctxExecucao) {

        }

    }
}
