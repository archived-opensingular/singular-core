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
        if (tipo instanceof MTipoComposto) {
            MTipo<?> campo = ((MTipoComposto) tipo).getCampo(leitor.getTrecho());
            if (campo == null) {
                throw new RuntimeException(leitor.getTextoErro(tipo, "Não existe o campo"));
            } else if (leitor.isUltimo()) {
                return campo;
            }
            return resolverTipoCampo(campo, leitor.proximo());
        } else if (tipo instanceof MTipoSimples) {
            throw new RuntimeException(leitor.getTextoErro(tipo, "Não se aplica um path a um tipo simples"));
        } else {
            throw new RuntimeException("Não implementado para " + tipo.getClass());
        }
    }

}
