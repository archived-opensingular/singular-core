/**
 * Mirante Tecnologia - Fábrica de Software
 * www.mirante.net.br
 */
package org.opensingular.singular.exemplos.notificacaosimplificada.domain.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Coutinho
 * @since 16/12/2011
 */
public enum TipoDecisao {

    DEFERIDO("D", "Deferido"),
    INDEFERIDO("I", "Indeferido"),
    COMITE("C", "Comite"),
    EXIGENCIA("E", "Exigencia"),
    NAO_SE_APLICA("X", "Não se aplica"),
    DAR_PROVIMENTO("P", "Dar Provimento"),
    NEGAR_PROVIMENTO("N", "Negar Provimento"),
    DAR_PROVIMENTO_PARCIAL("V", "Dar Provimento Parcial"),
    RETRATACAO("R", "Retratação"),
    NAO_RETRATACAO("T", "Não retratação");

    private String codigo;
    private String descricao;

    private TipoDecisao(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the descricao
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * @param descricao the descricao to set
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public static TipoDecisao valueOfEnum(String codigo) {
        TipoDecisao status[] = TipoDecisao.values();

        for (TipoDecisao st : status) {
            if (codigo != null && codigo.equals(st.getCodigo())) {
                return st;
            }
        }
        return null;
    }

    public static List<TipoDecisao> listarTipoRecurso() {
        List<TipoDecisao> list = new ArrayList<TipoDecisao>();

        list.add(TipoDecisao.DAR_PROVIMENTO);
        list.add(TipoDecisao.NEGAR_PROVIMENTO);

        return list;
    }

    public static List<TipoDecisao> listarTipoRecursoDespacho(boolean isGerente) {
        List<TipoDecisao> list = new ArrayList<TipoDecisao>();

        list.add(TipoDecisao.RETRATACAO);
        list.add(TipoDecisao.NAO_RETRATACAO);
        if (!isGerente) {
            list.add(TipoDecisao.NAO_SE_APLICA);
        }

        return list;
    }

    /**
     * Verifica se a decisão é igual a: {@link #TipoDecisao#DEFERIDO ou {
     *
     * @param decisao - {@link TipoDecisao}
     * @return {@link Boolean}
     * @link #TipoDecisao#INDEFERIDO}
     */
    public static boolean isEqualDeferidoOrIndeferido(TipoDecisao decisao) {
        return DEFERIDO.equals(decisao)
                || INDEFERIDO.equals(decisao);
    }

    /**
     * Verifica se a decisão selecionada é Indeferida {@link #TipoDecisao
     * #INDEFERIDO}
     *
     * @return {@link Boolean}
     */
    public boolean seIndeferido() {
        return INDEFERIDO.getCodigo().equals(getCodigo());
    }

    /**
     * Verifica se a decisão selecionada é Deferida {@link #TipoDecisao#DEFERIDO}
     *
     * @return {@link Boolean}
     */
    public boolean seDeferido() {
        return DEFERIDO.getCodigo().equals(getCodigo());
    }

    /**
     * Verifica se a decisão selecionada é Exigência {@link #TipoDecisao#DEFERIDO}
     *
     * @return {@link Boolean}
     */
    public boolean seExigencia() {
        return EXIGENCIA.getCodigo().equals(getCodigo());
    }

}
