package org.opensingular.singular.form.io;

import org.opensingular.singular.form.ICompositeInstance;
import org.opensingular.singular.form.SAttributeEnabled;
import org.opensingular.singular.form.SInstance;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Métodos utilitários para testar instâncias e tipos.
 *
 * @author Daniel C. Bordin
 */
public class FormAssert {

    private FormAssert() {
    }

    public static void assertEquivalentInstance(SInstance original, SInstance novo) {
        assertEquivalentInstance(original, novo, true);
    }

    public static void assertEquivalentInstance(SInstance original, SInstance copy, boolean mustHaveSameId) {
        try {
            assertNotSame(original, copy);
            assertEquals(original.getClass(), copy.getClass());
            assertEquals(original.getType().getName(), copy.getType().getName());
            assertEquals(original.getType().getClass(), copy.getType().getClass());
            assertEquals(original.getName(), copy.getName());
            if (mustHaveSameId) {
                assertEquals(original.getId(), copy.getId());
            }
            assertEquals(original.getPathFull(), copy.getPathFull());
            if (original.getParent() != null) {
                assertNotNull(copy.getParent());
                assertEquals(original.getParent().getPathFull(), copy.getParent().getPathFull());
            } else {
                assertNull(copy.getParent());
            }
            // if (false && original instanceof SIComposite) {
            // SIComposite originalC = (SIComposite) original;
            // SIComposite novoC = (SIComposite) copy;
            // for (SInstance field : originalC.getFields()) {
            // assertEquivalent(field, novoC.getField(field.getName()));
            // }
            // for (SInstance field : novoC.getFields()) {
            // assertEquivalent(originalC.getField(field.getName()), field);
            // }
            // }
            if (original instanceof ICompositeInstance) {
                List<SInstance> filhosOriginal = new ArrayList<>(((ICompositeInstance) original).getChildren());
                List<SInstance> filhosNovo = new ArrayList<>(((ICompositeInstance) copy).getChildren());
                assertEquals(filhosOriginal.size(), filhosNovo.size());
                for (int i = 0; i < filhosOriginal.size(); i++) {
                    assertEquivalentInstance(filhosOriginal.get(0), filhosNovo.get(0), mustHaveSameId);
                }
            } else {
                assertEquals(original.getValue(), copy.getValue());
            }

            assertEquals(original.isAttribute(), copy.isAttribute());
            if(! original.isAttribute()) {
                assertEqualsAttributes(original, copy);
            }
        } catch (AssertionError e) {
            if (e.getMessage().startsWith("Erro comparando")) {
                throw e;
            }
            throw new AssertionError("Erro comparando '" + original.getPathFull() + "'", e);
        }
    }

    public static void assertEqualsAttributes(SAttributeEnabled original, SAttributeEnabled copy) {
        try {
            assertEquals(original.getAttributes().size(), copy.getAttributes().size());

            for (SInstance atrOriginal : original.getAttributes()) {
                SInstance atrNovo = copy.getAttribute(atrOriginal.getAttributeInstanceInfo().getName()).get();
                try {
                    assertNotNull(atrNovo);
                    assertEquivalentInstance(atrOriginal, atrNovo, false);
                } catch (AssertionError e) {
                    throw new AssertionError(
                            "Erro comparando atributo '" + atrOriginal.getAttributeInstanceInfo().getName() + "'", e);
                }
            }
        } catch (AssertionError e) {
            if (e.getMessage().startsWith("Erro comparando atributos de ")) {
                throw e;
            }
            throw new AssertionError("Erro comparando atributos de '" + original + "'", e);
        }
    }
}
