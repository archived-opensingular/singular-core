package org.opensingular.singular.form;

import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import junit.framework.TestCase;
import org.junit.runners.Parameterized;

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

    public static void assertException(Runnable acao, String trechoMsgEsperada) {
        assertException(acao, RuntimeException.class, trechoMsgEsperada, null);
    }

    public static void assertException(Runnable acao, String trechoMsgEsperada, String msgFailException) {
        assertException(acao, RuntimeException.class, trechoMsgEsperada, msgFailException);
    }

    public static void assertException(Runnable acao, Class<? extends Exception> exceptionEsperada) {
        assertException(acao, exceptionEsperada, null, null);
    }

    public static void assertException(Runnable acao, Class<? extends Exception> exceptionEsperada,
            String trechoMsgEsperada) {
        assertException(acao, exceptionEsperada, trechoMsgEsperada, null);
    }

    public static void assertException(Runnable acao, Class<? extends Exception> exceptionEsperada, String trechoMsgEsperada,
            String msgFailException) {
        try {
            acao.run();
            String msg = "Não ocorreu nenhuma Exception. Era esperado " + exceptionEsperada.getSimpleName() + "'";
            if (trechoMsgEsperada != null) {
                msg += " com mensagem contendo '" + trechoMsgEsperada + "'";
            }
            if (msgFailException != null) {
                msg += ", pois " + msgFailException;
            }
            fail(msg);
        } catch (Exception e) {
            if (exceptionEsperada.isInstance(e)) {
                if (trechoMsgEsperada == null || (e.getMessage() != null && e.getMessage().contains(trechoMsgEsperada))) {
                    return;
                }
            }
            throw e;
        }

    }

    public SInstance createSerializableTestInstance(Class<? extends SType<?>> typeClass) {
        return createSerializableTestInstance(getDictionaryFactory(), typeClass);
    }

    public static SInstance createSerializableTestInstance(Supplier<SDictionary> dictionaryFactory,
            Class<? extends SType<?>> typeClass) {
        RefType refType = new RefType() {
            @Override
            protected SType<?> retrieve() {
                return dictionaryFactory.get().getType(typeClass);
            }
        };
        return SDocumentFactory.empty().createInstance(refType);
    }

    public SInstance createSerializableTestInstance(String typeName, ConfiguratorTestPackage setupCode) {
        return createSerializableTestInstance(getDictionaryFactory(), typeName, setupCode);
    }

    public static SInstance createSerializableTestInstance(Supplier<SDictionary> dictionaryFactory, String typeName,
            ConfiguratorTestPackage setupCode) {
        RefType refType = new RefType() {

            @Override
            protected SType<?> retrieve() {
                SDictionary dictionary = dictionaryFactory.get();
                setupCode.setup(dictionary.createNewPackage("teste"));
                return dictionary.getType(typeName);
            }
        };
        return SDocumentFactory.empty().createInstance(refType);
    }

    public interface ConfiguratorTestPackage extends Serializable {
        public void setup(PackageBuilder pkg);
    }
}
