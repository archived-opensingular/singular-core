package br.net.mirante.singular.dto;

import br.net.mirante.singular.flow.core.dto.IDefinitionDTO;
import br.net.mirante.singular.util.FormatUtil;

public class DefinitionDTO implements IDefinitionDTO {

    private Long cod;
    private String nome;
    private String sigla;
    private String categoria;
    private String codGrupo;
    private Long quantidade;
    private Long tempoMedio;
    private Long throughput;
    private Long version;

    public DefinitionDTO() {
    }
    
    public DefinitionDTO(Long cod, String nome, String sigla, String categoria,
        Long codGrupo, Long quantidade, Long tempoMedio, Long throughput) {
        this.cod = cod;
        this.nome = nome;
        this.sigla = sigla;
        this.categoria = categoria;
        this.quantidade = quantidade;
        this.tempoMedio = tempoMedio;
        this.throughput = throughput;
        this.version = 1L;
    }

    public String getCodGrupo() {
        return codGrupo;
    }

    public void setCodGrupo(String codGrupo) {
        this.codGrupo = codGrupo;
    }

    @Override
    public Long getCod() {
        return cod;
    }

    @Override
    public void setCod(Long cod) {
        this.cod = cod;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String getSigla() {
        return sigla;
    }

    @Override
    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    @Override
    public String getCategoria() {
        return categoria;
    }

    @Override
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public Long getQuantidade() {
        return quantidade;
    }

    @Override
    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public Long getTempoMedio() {
        return tempoMedio;
    }

    @Override
    public void setTempoMedio(Long tempoMedio) {
        this.tempoMedio = tempoMedio;
    }

    @Override
    public String getTempoMedioString() {
        StringBuilder tempo = new StringBuilder("");
        if (this.tempoMedio != null) {
            FormatUtil.appendSeconds(tempo, this.tempoMedio);
        }
        return tempo.toString();
    }

    @Override
    public Long getThroughput() {
        return throughput;
    }

    @Override
    public void setThroughput(Long throughput) {
        this.throughput = throughput;
    }

    @Override
    public Long getVersion() {
        return version;
    }

    @Override
    public void setVersion(Long version) {
        this.version = version;
    }
}
