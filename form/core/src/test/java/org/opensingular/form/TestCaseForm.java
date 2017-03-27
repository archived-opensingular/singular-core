package org.opensingular.form;

import junit.framework.TestCase;
import org.junit.runners.Parameterized;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.helpers.AssertionsSType;
import org.opensingular.internal.lib.commons.test.SingularTestUtil;
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
        List<TestFormConfig> executionParams = new ArrayList<>();
        addScenario(executionParams, "default option", () -> SDictionary.create());
        return executionParams;
    }

    private static void addScenario(List<TestFormConfig> executionParams, String name, SerializableSupplier factory) {
        executionParams.add(new TestFormConfig(name, factory));
    }

    private static interface SerializableSupplier<T> extends Supplier<T>, Serializable {
    }

    ;

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
        SInstance raiz = registro.getDocument().getRoot();
        if (caminho == null) {
            esperadoFull = raiz.getName();
        } else if (raiz instanceof SIList) {
            esperadoFull = raiz.getName() + caminho;
        } else {
            esperadoFull = raiz.getName() + "." + caminho;
        }
        assertEquals(esperadoFull, esperada.getPathFull());

        if (caminho != null) {
            assertEquals(esperada, ((ICompositeInstance) registro.getDocument().getRoot()).getField(caminho));
        }
    }

    protected static <R extends SInstance & ICompositeInstance> void testAtribuicao(R registro, String path, Object valor,
                                                                                    int qtdFilhosEsperados) {
        testAtribuicao(registro, path, valor);
        assertFilhos(registro, qtdFilhosEsperados);
    }

    protected static <R extends SInstance & ICompositeInstance> void testAtribuicao(R registro, String path, Object valor) {
        registro.setValue(path, valor);
        assertEquals(valor, registro.getValue(path));
    }

    protected static void assertEqualsList(Object valor, Object... valoresEsperados) {
        if (!(valor instanceof List)) {
            throw new RuntimeException("Não é uma lista");
        }
        List<?> valores = (List<?>) valor;
        assertEquals(valoresEsperados.length, valores.size());
        for (int i = 0; i < valoresEsperados.length; i++) {
            if (!Objects.equals(valoresEsperados[i], valores.get(i))) {
                throw new RuntimeException(
                        "Valores diferentes na posição " + i + ": era esparado " + valoresEsperados[i] + " e veio " + valores.get(i));
            }
        }
    }

    /**
     * Faz alguns verifições quanto a integridade dos filhos;
     */
    protected static void assertFilhos(SInstance pai, int qtdFilhosEsperados) {
        int[] counter = new int[1];
        assertNotNull(pai.getDocument());
        assertFilhos(pai, pai, counter);
        assertEquals(qtdFilhosEsperados, counter[0]);

        SInstance atual = pai;
        while (atual != null) {
            assertEquals(pai.getDocument(), atual.getDocument());
            if (atual.getParent() == null) {
                assertEquals(atual, pai.getDocument().getRoot());
            }
            atual = atual.getParent();
        }

    }

    private static void assertFilhos(SInstance raiz, SInstance pai, int[] counter) {
        if (pai instanceof ICompositeInstance) {
            for (SInstance filho : ((ICompositeInstance) pai).getChildren()) {
                assertEquals(raiz.getDocument(), filho.getDocument());
                assertEquals(pai, filho.getParent());
                counter[0]++;
                assertFilhos(raiz, filho, counter);
            }
        }
    }

    @Deprecated
    public static void assertException(Runnable acao, String trechoMsgEsperada) {
        SingularTestUtil.assertException(acao, RuntimeException.class, trechoMsgEsperada, null);
    }

    @Deprecated
    public static void assertException(Runnable acao, String trechoMsgEsperada, String msgFailException) {
        SingularTestUtil.assertException(acao, RuntimeException.class, trechoMsgEsperada, msgFailException);
    }

    @Deprecated
    public static void assertException(Runnable acao, Class<? extends Exception> exceptionEsperada) {
        SingularTestUtil.assertException(acao, exceptionEsperada, null, null);
    }

    @Deprecated
    public static void assertException(Runnable acao, Class<? extends Exception> exceptionEsperada,
            String trechoMsgEsperada) {
        SingularTestUtil.assertException(acao, exceptionEsperada, trechoMsgEsperada, null);
    }

    @Deprecated
    public static void assertException(Runnable acao, Class<? extends Exception> exceptionEsperada, String trechoMsgEsperada,
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
