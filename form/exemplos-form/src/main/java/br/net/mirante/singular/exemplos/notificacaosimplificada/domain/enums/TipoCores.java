package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums;

public enum TipoCores {

    VERDE("D", "Verde"),
    VERMELHO("M", "Vermelho"),
    AMARELO("A", "Amarelo"),
    NULO("X", "Nulo");

    private String codigo;
    private String descricao;

    private TipoCores(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    /**
     * @return codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @return descricao
     */
    public String getDescricao() {
        return descricao;
    }


    public static TipoCores valueOfEnum(String codigo) {
        TipoCores status[] = TipoCores.values();

        for (TipoCores st : status) {
            if (codigo != null && codigo.equals(st.getCodigo())) {
                return st;
            }
        }
        return null;
    }
}
