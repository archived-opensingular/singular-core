package org.opensingular.form;

import junit.framework.TestCase;
import org.junit.runners.Parameterized;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.helpers.AssertionsSType;
import org.opensingular.internal.lib.commons.test.RunnableEx;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
import org.opensingular.lib.commons.context.ServiceRegistryLocator;
import org.opensingular.lib.commons.util.Loggable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class TestCaseForm extends TestCase implements Loggable {

    private final Supplier<SDictionary> dictionaryFactory;

    @Parameterized.Parameters(name = "{index}: ({0})")
    public static Collection<TestFormConfig> data() {
        ServiceRegistryLocator.setup(new ServiceRegistryLocator());
        List<TestFormConfig> executionParams = new ArrayList<>();
        addScenario(executionParams, "default option", () -> SDictionary.create());
        return executionParams;
    }

    private static void addScenario(List<TestFormConfig> executionParams, String name, SerializableSupplier factory) {
        executionParams.add(new TestFormConfig(name, factory));
    }

    private static interface SerializableSupplier<T> extends Supplier<T>, Serializable {
    }

    protected static class TestFormConfig {
        private final String scenarioName;

        private final Supplier<SDictionary> dictionaryFactory;

        public TestFormConfig(String scenarioName, Supplier<SDictionary> dictionaryFactory) {
            this.scenarioName = scenarioName;
            this.dictionaryFactory = dictionaryFactory;
        }

        public Supplier<SDictionary> getDictionaryFactory() {
            return dictionaryFactory;
        }

        @Override
        public String toString() {
            return "Cenário '" + scenarioName + '\'';
        }
    }

    public TestCaseForm(TestFormConfig testFormConfig) {
        this.dictionaryFactory = testFormConfig.getDictionaryFactory();
    }

    protected final Supplier<SDictionary> getDictionaryFactory() {
        return dictionaryFactory;
    }

    protected final SDictionary createTestDictionary() {
        return dictionaryFactory.get();
    }

    protected final PackageBuilder createTestPackage() {
        return createTestPackage("teste");
    }

    protected final PackageBuilder createTestPackage(String packageName) {
        return createTestDictionary().createNewPackage(packageName);
    }

    /** Cria assertivas para um {@link SType}. */
    public static AssertionsSType assertType(SType<?> type) {
        return new AssertionsSType(type);
    }

    /** Cria assertivas para um {@link SInstance}. */
    public static AssertionsSInstance assertInstance(SInstance instance) {
        return new AssertionsSInstance(instance);
    }

    protected static void testCaminho(SInstance registro, String path, String caminhoCompletoEsperado) {
        SInstance esperada = (path == null) ? registro : ((ICompositeInstance) registro).getField(path);
        assertNotNull(esperada);
        String caminho = esperada.getPathFromRoot();
        assertEquals(caminhoCompletoEsperado, caminho);

        String esperadoFull;
        SInstance root = registro.getDocument().getRoot();
        if (caminho == null) {
            esperadoFull = root.getName();
        } else if (root instanceof SIList) {
            esperadoFull = root.getName() + caminho;
        } else {
            esperadoFull = root.getName() + "." + caminho;
        }
        assertEquals(esperadoFull, esperada.getPathFull());

        if (caminho != null) {
            assertEquals(esperada, ((ICompositeInstance) registro.getDocument().getRoot()).getField(caminho));
        }
    }

    protected static <R extends SInstance & ICompositeInstance> void testAtribuicao(R registro, String path, Object valor,
                                                                                    int qtdExpectedChildren) {
        testAtribuicao(registro, path, valor);
        assertChildren(registro, qtdExpectedChildren);
    }

    protected static <R extends SInstance & ICompositeInstance> void testAtribuicao(R registro, String path, Object valor) {
        registro.setValue(path, valor);
        assertEquals(valor, registro.getValue(path));
    }

    protected static void assertEqualsList(Object value, Object... expectedValues) {
        if (!(value instanceof List)) {
            throw new RuntimeException("Não é uma lista");
        }
        List<?> values = (List<?>) value;
        assertEquals(expectedValues.length, values.size());
        for (int i = 0; i < expectedValues.length; i++) {
            if (!Objects.equals(expectedValues[i], values.get(i))) {
                throw new RuntimeException(
                        "Valores diferentes na posição " + i + ": era esparado " + expectedValues[i] + " e veio " + values.get(i));
            }
        }
    }

    /**
     * Faz alguns verifições quanto a integridade dos filhos;
     */
    protected static void assertChildren(SInstance parent, int qtdExpectChildren) {
        int[] counter = new int[1];
        assertNotNull(parent.getDocument());
        assertChildren(parent, parent, counter);
        assertEquals(qtdExpectChildren, counter[0]);

        SInstance current = parent;
        while (current != null) {
            assertEquals(parent.getDocument(), current.getDocument());
            if (current.getParent() == null) {
                assertEquals(current, parent.getDocument().getRoot());
            }
            current = current.getParent();
        }

    }

    private static void assertChildren(SInstance root, SInstance parent, int[] counter) {
        if (parent instanceof ICompositeInstance) {
            for (SInstance child : ((ICompositeInstance) parent).getChildren()) {
                assertEquals(root.getDocument(), child.getDocument());
                assertEquals(parent, child.getParent());
                counter[0]++;
                assertChildren(root, child, counter);
            }
        }
    }

    @Deprecated
    public static void assertException(RunnableEx acao, String trechoMsgEsperada) {
        SingularTestUtil.assertException(acao, RuntimeException.class, trechoMsgEsperada, null);
    }

    @Deprecated
    public static void assertException(RunnableEx acao, String trechoMsgEsperada, String msgFailException) {
        SingularTestUtil.assertException(acao, RuntimeException.class, trechoMsgEsperada, msgFailException);
    }

    @Deprecated
    public static void assertException(RunnableEx acao, Class<? extends Exception> exceptionEsperada) {
        SingularTestUtil.assertException(acao, exceptionEsperada, null, null);
    }

    @Deprecated
    public static void assertException(RunnableEx acao, Class<? extends Exception> exceptionEsperada,
            String trechoMsgEsperada) {
        SingularTestUtil.assertException(acao, exceptionEsperada, trechoMsgEsperada, null);
    }

    @Deprecated
    public static void assertException(RunnableEx acao, Class<? extends Exception> exceptionEsperada, String trechoMsgEsperada,
            String msgFailException) {
        SingularTestUtil.assertException(acao, exceptionEsperada, trechoMsgEsperada, msgFailException);
    }

    public SInstance createSerializableTestInstance(Class<? extends SType<?>> typeClass) {
        return createSerializableTestInstance(getDictionaryFactory(), typeClass);
    }

    public static SInstance createSerializableTestInstance(Supplier<SDictionary> dictionaryFactory,
            Class<? extends SType<?>> typeClass) {
        RefType refType = RefType.of(() -> dictionaryFactory.get().getType(typeClass));
        return SDocumentFactory.empty().createInstance(refType);
    }

    public SInstance createSerializableTestInstance(String typeName, ConfiguratorTestPackage setupCode) {
        return createSerializableTestInstance(getDictionaryFactory(), typeName, setupCode);
    }

    public static SInstance createSerializableTestInstance(Supplier<SDictionary> dictionaryFactory, String typeName,
            ConfiguratorTestPackage setupCode) {
        RefType refType = RefType.of(() -> {
            SDictionary dictionary = dictionaryFactory.get();
            setupCode.setup(dictionary.createNewPackage("teste"));
            return dictionary.getType(typeName);
        });
        return SDocumentFactory.empty().createInstance(refType);
    }

    public interface ConfiguratorTestPackage extends Serializable {
        public void setup(PackageBuilder pkg);
    }
}
