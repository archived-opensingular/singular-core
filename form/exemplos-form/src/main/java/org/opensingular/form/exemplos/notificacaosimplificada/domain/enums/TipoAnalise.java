/**
 * Mirante Tecnologia - Fábrica de Software
 * www.mirante.net.br
 */
package org.opensingular.form.exemplos.notificacaosimplificada.domain.enums;

/**
 * Tipos de análise realizadas para diferenciar uma anális realizada pelo Gerente Geral das outras análises relacionadas
 * a um processo
 *
 * @author Lucas Souza <lucas.souza@mirante.com.br>
 */
public enum TipoAnalise {

    COMUM(1, "Comum"),
    FINAL(2, "Final"),
    ESPECIALISTA(3, "Especialista"),
    COREC(4, "Corec"),
    ESPECIALISTA_RETRATACAO(5, "Especialista Retratação"),
    COMUM_RETRATACAO_COORDENADOR(6, "Comum Retratação Coordenador"),
    FINAL_RETRATACAO(7, "Final Retratação"),
    DICOL(8, "DICOL"),
    COMUM_RETRATACAO_GERENTE(9, "Comum Retratação Gerente");

    private Integer codigo;
    private String  descricao;

    private TipoAnalise(Integer codigo, String descricao) {
        this.setCodigo(codigo);
        this.setDescricao(descricao);
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static TipoAnalise valueOfEnum(Integer codigo) {
        TipoAnalise status[] = TipoAnalise.values();

        for (TipoAnalise st : status) {
            if (codigo != null && codigo.equals(st.getCodigo())) {
                return st;
            }
        }
        return null;
    }

}
