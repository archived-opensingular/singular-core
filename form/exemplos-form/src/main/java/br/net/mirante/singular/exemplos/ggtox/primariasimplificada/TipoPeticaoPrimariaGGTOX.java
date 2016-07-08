package br.net.mirante.singular.exemplos.ggtox.primariasimplificada;


import java.util.stream.Stream;

public enum TipoPeticaoPrimariaGGTOX {

    BIOLOGICO(1, "Feromônio, produtos biológicos, bioquímicos e outros"),
    PRE_MISTURA(2, "Pré-mistura"),
    PRESERVATIVO_MATEIRA(3, "Preservativo de madeira"),
    NAO_AGRICOLA(4, "Produto de uso não agrícola"),
    PF(5, "Produto formulado de ingrediente ativo já registrado (PF)"),
    PFE(6, "Produto formulado com base em produto técnico equivalente (PFE)"),
    PT(7, "Produto técnico de ingrediente ativo já registrado (PT)"),
    PTE(8, "Produto técnico equivalente (PTE)");

    private Integer id;
    private String  descricao;

    TipoPeticaoPrimariaGGTOX(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoPeticaoPrimariaGGTOX getValueById(Integer id) {
        return Stream.of(values()).filter(t -> t.id.equals(id)).findFirst().orElse(null);
    }

}
