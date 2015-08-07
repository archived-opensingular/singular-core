package br.net.mirante.singular.flow.core.entity;

import java.io.Serializable;
import java.util.List;

public interface IEntityProcess extends IEntityByCod, Serializable {

    String getNome();

    void setNome(String nome);

    String getNomeClasseDefinicao();

    void setNomeClasseDefinicao(String nomeClasseDefinicao);

    String getSigla();

    void setSigla(String sigla);

    void setAtivo(boolean ativo);

    boolean isAtivo();

    IEntityCategory getCategoria();

    List<? extends IEntityTaskDefinition> getSituacoes();

    List<? extends IEntityProcessRole> getPapeis();

    default IEntityTaskDefinition getSituacao(String sigla) {
        for (IEntityTaskDefinition situacao : getSituacoes()) {
            if (situacao.getSigla().equalsIgnoreCase(sigla)) {
                return situacao;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    default <X extends IEntityProcessRole> X getPapel(String sigla) {
        for (IEntityProcessRole papel : getPapeis()) {
            if (papel.getSigla().equalsIgnoreCase(sigla)) {
                return (X) papel;
            }
        }
        return null;
    }
}
