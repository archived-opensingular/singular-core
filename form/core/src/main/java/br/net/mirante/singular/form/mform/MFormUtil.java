package br.net.mirante.singular.form.mform;

import javax.lang.model.SourceVersion;

public class MFormUtil {

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
                return ((MTipoLista<?>) tipo).getTipoElementos();
            }
            throw new RuntimeException(leitor.getTextoErro(tipo, "Não se aplica a um tipo lista"));
        } else if (tipo instanceof MTipoSimples) {
            throw new RuntimeException(leitor.getTextoErro(tipo, "Não se aplica um path a um tipo simples"));
        } else {
            throw new RuntimeException(leitor.getTextoErro(tipo, "Não implementado para " + tipo.getClass()));
        }
    }

}
