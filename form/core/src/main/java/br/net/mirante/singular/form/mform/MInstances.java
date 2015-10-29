package br.net.mirante.singular.form.mform;

import java.util.function.Consumer;

/**
 * Métodos utilitários para manipulação de MInstance.
 *
 * @author Daniel C. Bordin
 */
public class MInstances {

    private MInstances() {
    }

    /**
     * Percorre todos as instâncias filha da instancia informada chamando o
     * consumidor, incundo os filhos dos filhos. Ou seja, faz um pecorrimento em
     * profundidade. Não chama o consumidor para a instância raiz.
     */
    public static void visitAllChildren(MInstancia parent, Consumer<MInstancia> consumer) {
        if (parent instanceof ICompositeInstance) {
            for (MInstancia child : ((ICompositeInstance) parent).getChildren()) {
                consumer.accept(child);
                visitAllChildren(child, consumer);
            }
        }
    }

    /**
     * Percorre a instância innformada e todos as instâncias filha da instancia
     * informada chamando o consumidor, incundo os filhos dos filhos. Ou seja,
     * faz um pecorrimento em profundidade.
     */
    public static void visitAll(MInstancia instance, Consumer<MInstancia> consumer) {
        consumer.accept(instance);
        if (instance instanceof ICompositeInstance) {
            for (MInstancia child : ((ICompositeInstance) instance).getChildren()) {
                visitAllChildren(child, consumer);
            }
        }
    }
}
