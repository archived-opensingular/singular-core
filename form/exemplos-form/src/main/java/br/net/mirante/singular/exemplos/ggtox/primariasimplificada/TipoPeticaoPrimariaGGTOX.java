package br.net.mirante.singular.exemplos.ggtox.primariasimplificada;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum TipoPeticaoPrimariaGGTOX {

    BIOLOGICO(1, "Feromônio, produtos biológicos, bioquímicos e outros"),
    PRE_MISTURA(2, "Pré-mistura") {
        @Override
        public List<String> niveis() {
            return Arrays.asList("I", "II", "III");
        }
    },
    PRESERVATIVO_MADEIRA(3, "Preservativo de madeira") {
        @Override
        public List<String> niveis() {
            return Arrays.asList("I", "II");
        }
    },
    NAO_AGRICOLA(4, "Produto de uso não agrícola") {
        @Override
        public List<String> niveis() {
            return Arrays.asList("I", "II", "III");
        }
    },
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

    public List<String> niveis() {
        return Arrays.asList("I", "II", "III", "IV");
    }

}