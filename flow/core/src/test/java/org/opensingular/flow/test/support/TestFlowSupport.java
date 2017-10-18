package org.opensingular.flow.test.support;

import org.apache.commons.lang3.RandomUtils;
import org.hibernate.SessionFactory;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized;
import org.junit.runners.model.InitializationError;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.ProcessDefinitionCache;
import org.opensingular.flow.core.SingularFlowConfigurationBean;
import org.opensingular.flow.core.TestProcessBeanInjection;
import org.opensingular.flow.test.TestDAO;
import org.opensingular.lib.commons.base.SingularPropertiesImpl;
import org.opensingular.lib.commons.context.ServiceRegistryLocator;
import org.opensingular.lib.commons.context.spring.SpringServiceRegistry;
import org.opensingular.lib.support.spring.util.ApplicationContextProvider;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@Transactional
@Rollback(value = false)
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(TestFlowSupport.FactoryRunnerParameteziedWithSpring.class)
@ActiveProfiles(resolver = TestFlowSupport.ParameterizedFlowProfileResolver.class)
public abstract class TestFlowSupport {


    @Parameterized.Parameter(0)
    public FlowTestConfig flowTestConfig;

    @Inject
    protected SingularFlowConfigurationBean mbpmBean;

    @Inject
    protected TestDAO testDAO;

    @Inject
    protected SessionFactory sessionFactory;

    @Inject
    private ApplicationContextProvider applicationContextProvider;

    protected static MyBean myBeanRef;

    @PostConstruct
    public void init() {
        configApplicationContext(applicationContextProvider.get());
    }

    public static void configApplicationContext(ApplicationContext applicationContext) {
        ServiceRegistryLocator.setup(new SpringServiceRegistry());
        try {
            myBeanRef = applicationContext.getBean(TestProcessBeanInjection.MyBean.class);
        } catch (NoSuchBeanDefinitionException e) {
            myBeanRef = null;
        }
        if (myBeanRef == null) {
            myBeanRef = new MyBean();
            ((ConfigurableApplicationContext) applicationContext).getBeanFactory().registerSingleton(
                    TestProcessBeanInjection.MyBean.class.getName(), myBeanRef);
        }
    }

    protected static AssertionsProcessInstance assertions(FlowInstance target) {
        return new AssertionsProcessInstance(target);
    }

    @BeforeClass
    public static void invalidateCache(){
        ProcessDefinitionCache.invalidateAll();
    }

    @Parameterized.Parameters(name = "{index}: ({0})")
    public static Collection<FlowTestConfig> data() {
        SingularPropertiesImpl.get().reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource(
                "singular-mssql.properties"));

        List<FlowTestConfig> executionParams = new ArrayList<>();
        executionParams.add(new FlowTestConfig("mssql", "singular-mssql.properties"));
        executionParams.add(new FlowTestConfig("oracle", "singular-ora.properties"));
        return executionParams;
    }


    /** Cria um factory de runners que é ao mesmo tempo parametrizada e integrada com o Spring. */
    public static class FactoryRunnerParameteziedWithSpring implements ParametersRunnerFactory {
        @Override
        public Runner createRunnerForTestWithParameters(final TestWithParameters test) throws InitializationError {
            return new RunnerParametersWithSpring2(test);
        }
    }

    /** Cria um executor de test que é parametrizada e ao mesmo tempo faz o setup do spring usando profiles. */
    private static class RunnerParametersWithSpring2 extends SpringJUnit4ClassRunner {

        private final FlowTestConfig flowTestConfig;

        private final  BlockJUnit4ClassRunnerWithParameters runnerParam;

        public RunnerParametersWithSpring2(TestWithParameters test) throws InitializationError {
            super(prepare(test));
            ParameterizedFlowProfileResolver.currentProfile = null;
            runnerParam = new BlockJUnit4ClassRunnerWithParameters(test);
            flowTestConfig = (FlowTestConfig) test.getParameters().get(0);
        }

        private static Class<?> prepare(TestWithParameters test) {
            ParameterizedFlowProfileResolver.currentProfile = ((FlowTestConfig) test.getParameters().get(0)).getSpringProfile();
            return test.getTestClass().getJavaClass();
        }

        @Override
        protected Object createTest() throws Exception {
            SingularPropertiesImpl.get().reloadAndOverrideWith(ClassLoader.getSystemClassLoader().getResource(flowTestConfig.getBdProperties()));
            Object testInstance = runnerParam.createTest();
            getTestContextManager().prepareTestInstance(testInstance);
            return testInstance;
        }

        //Esse método roda antes do contexto do spring ser reinicializado.
        public void run(RunNotifier notifier) {
            ParameterizedFlowProfileResolver.currentProfile = flowTestConfig.getSpringProfile();
            super.run(notifier);
            ParameterizedFlowProfileResolver.currentProfile = null;
        }

        @Override
        protected String getName() {
            return runnerParam.getDescription().getDisplayName();
        }
    }

    public static class ParameterizedFlowProfileResolver implements ActiveProfilesResolver {

        static String currentProfile;

        @Override
        public String[] resolve(Class<?> testClass) {
            Objects.requireNonNull(currentProfile);
            return new String[] {currentProfile};
        }
    }

    public static class MyBean {

        private final long v = RandomUtils.nextLong(0, Long.MAX_VALUE);

        public long getV() {
            return v;
        }
    }
}
