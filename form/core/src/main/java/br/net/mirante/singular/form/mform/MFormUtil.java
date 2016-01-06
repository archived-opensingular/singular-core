package br.net.mirante.singular.form.mform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import javax.lang.model.SourceVersion;

import org.apache.commons.lang3.StringUtils;

import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;

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

    public static MTipo<?> resolverTipoCampo(MTipo<?> tipo, LeitorPath leitor) {
        while (!leitor.isEmpty()) {
            tipo = resolverTipoCampoInterno(tipo, leitor);
            leitor = leitor.proximo();
        }
        return tipo;
    }

    private static MTipo<?> resolverTipoCampoInterno(MTipo<?> tipo, LeitorPath leitor) {
        if (tipo instanceof MTipoComposto) {
            if (leitor.isIndice()) {
                throw new RuntimeException(leitor.getTextoErro(tipo, "Índice de lista não se aplica a um tipo composto"));
            }
            MTipo<?> campo = ((MTipoComposto<?>) tipo).getCampo(leitor.getTrecho());
            if (campo == null) {
                throw new RuntimeException(leitor.getTextoErro(tipo, "Não existe o campo '" + leitor.getTrecho() + "'"));
            }
            return campo;
        } else if (tipo instanceof MTipoLista) {
            if (leitor.isIndice()) {
                return ((MTipoLista<?, ?>) tipo).getTipoElementos();
            }
            throw new RuntimeException(leitor.getTextoErro(tipo, "Não se aplica a um tipo lista"));
        } else if (tipo instanceof MTipoSimples) {
            throw new RuntimeException(leitor.getTextoErro(tipo, "Não se aplica um path a um tipo simples"));
        } else {
            throw new RuntimeException(leitor.getTextoErro(tipo, "Não implementado para " + tipo.getClass()));
        }
    }

    /**
     * Retorna o nome do filho atual indo em direção ao raiz mas parando segundo
     * a condicão de parada informada.
     */
    final static String generatePath(MInstancia atual, Predicate<MInstancia> condicaoDeParada) {
        List<MInstancia> sequencia = null;
        while (!condicaoDeParada.test(atual)) {
            if (sequencia == null) {
                sequencia = new ArrayList<>();
            }
            sequencia.add(atual);
            atual = atual.getPai();
        }
        if (sequencia != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = sequencia.size() - 1; i != -1; i--) {
                atual = sequencia.get(i);
                if (atual.getPai() instanceof MILista) {
                    int pos = ((MILista<?>) atual.getPai()).indexOf(atual);
                    if (pos == -1) {
                        throw new SingularFormException("Filho não encontrado");
                    }
                    sb.append('[').append(pos).append(']');
                } else {
                    if (atual.getPai() != null && sb.length() != 0) {
                        sb.append('.');
                    }
                    sb.append(atual.getNome());
                }
            }
            return sb.toString();
        }
        return null;
    }

    public static String generateUserFriendlyPath(MInstancia instance) {
        return generateUserFriendlyPath(instance, null);
    }
    public static String generateUserFriendlyPath(MInstancia instance, MInstancia parentContext) {
        LinkedList<String> labels = new LinkedList<>();
        for (MInstancia node = instance; (node != null) && (node != parentContext); node = node.getPai()) {
            String label = node.as(MPacoteBasic.aspect()).getLabel();
            if (StringUtils.isNotBlank(label))
                labels.add(label);
        }
        Collections.reverse(labels);

        if (!labels.isEmpty())
            return StringUtils.join(labels, " > ");
        else
            return "[" + instance.getNome() + "]";
    }
}
