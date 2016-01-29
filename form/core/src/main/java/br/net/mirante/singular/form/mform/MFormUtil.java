package br.net.mirante.singular.form.mform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import javax.lang.model.SourceVersion;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;

public final class MFormUtil {

    public static boolean isNomeSimplesValido(String nome) {
        return SourceVersion.isIdentifier(nome);
    }

    public static void checkNomeSimplesValido(String nome) {
        if (!isNomeSimplesValido(nome)) {
            throw new RuntimeException("'" + nome + "' não é um nome válido para tipo ou atributo");
        }
    }

    public static void checkNomePacoteValido(String nome) {
        if (!SourceVersion.isName(nome)) {
            throw new RuntimeException("'" + nome + "' não é um nome válido para um pacote");
        }
    }

    public static SType<?> resolverTipoCampo(SType<?> tipo, LeitorPath leitor) {
        while (!leitor.isEmpty()) {
            tipo = resolverTipoCampoInterno(tipo, leitor);
            leitor = leitor.proximo();
        }
        return tipo;
    }

    private static SType<?> resolverTipoCampoInterno(SType<?> tipo, LeitorPath leitor) {
        if (tipo instanceof STypeComposite) {
            if (leitor.isIndice()) {
                throw new RuntimeException(leitor.getTextoErro(tipo, "Índice de lista não se aplica a um tipo composto"));
            }
            SType<?> campo = ((STypeComposite<?>) tipo).getCampo(leitor.getTrecho());
            if (campo == null) {
                throw new RuntimeException(leitor.getTextoErro(tipo, "Não existe o campo '" + leitor.getTrecho() + "'"));
            }
            return campo;
        } else if (tipo instanceof STypeLista) {
            if (leitor.isIndice()) {
                return ((STypeLista<?, ?>) tipo).getTipoElementos();
            }
            throw new RuntimeException(leitor.getTextoErro(tipo, "Não se aplica a um tipo lista"));
        } else if (tipo instanceof STypeSimple) {
            throw new RuntimeException(leitor.getTextoErro(tipo, "Não se aplica um path a um tipo simples"));
        } else {
            throw new RuntimeException(leitor.getTextoErro(tipo, "Não implementado para " + tipo.getClass()));
        }
    }

    /**
     * Retorna o nome do filho atual indo em direção ao raiz mas parando segundo
     * a condicão de parada informada.
     */
    public final static String generatePath(SInstance atual, Predicate<SInstance> condicaoDeParada) {
        List<SInstance> sequencia = null;
        while (!condicaoDeParada.test(atual)) {
            if (sequencia == null) {
                sequencia = new ArrayList<>();
            }
            sequencia.add(atual);
            atual = atual.getParent();
        }
        if (sequencia != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = sequencia.size() - 1; i != -1; i--) {
                atual = sequencia.get(i);
                if (atual.getParent() instanceof SList) {
                    int pos = ((SList<?>) atual.getParent()).indexOf(atual);
                    if (pos == -1) {
                        throw new SingularFormException("Filho não encontrado");
                    }
                    sb.append('[').append(pos).append(']');
                } else {
                    if (atual.getParent() != null && sb.length() != 0) {
                        sb.append('.');
                    }
                    sb.append(atual.getNome());
                }
            }
            return sb.toString();
        }
        return null;
    }

    public static String generateUserFriendlyPath(SInstance instance) {
        return generateUserFriendlyPath(instance, null);
    }
    public static String generateUserFriendlyPath(SInstance instance, SInstance parentContext) {
        LinkedList<String> labels = new LinkedList<>();
        SInstance child = null;
        for (SInstance node = instance; node != null && !node.equals(parentContext); child = node, node = node.getParent()) {

            final String labelNode = node.as(SPackageBasic.aspect()).getLabel();

            if (node instanceof SList<?>) {
                SList<?> lista = (SList<?>) node;
                String labelLista = lista.as(SPackageBasic.aspect()).getLabel();
                int index = lista.indexOf(child) + 1;
                labels.add(labelLista + ((index > 0) ? " [" + (index) + "]" : ""));
            } else {
                if (StringUtils.isNotBlank(labelNode))
                    labels.add(labelNode);
            }
        }
        Collections.reverse(labels);

        if (!labels.isEmpty())
            return StringUtils.join(labels, " > ");
        else
            return null;
    }
}
