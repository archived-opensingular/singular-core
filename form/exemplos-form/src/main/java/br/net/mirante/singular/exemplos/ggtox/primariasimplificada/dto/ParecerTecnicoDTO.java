package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.dto;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

import static java.util.Optional.ofNullable;

public class ParecerTecnicoDTO implements Serializable {

    private String numero;
    private String nomeEmpresa;
    private String expediente;
    private String dataEntrada;
    private String assunto;
    private String numeroProcesso;
    private String nomeComercial;
    private String numeroRegistroMAPA;
    private String numeroProcessoAnvisa;
    private String produtoMatriz;
    private String tipoFormulacao;
    private String classificacaoToxicologia;
    private String ingredienteAtivo;
    private String monografia;
    private String nivel;
    private String dataProcolo;
    private String descricaoAssunto;
    private String codigoAssunto;
    private String cnpjEmpresa;
    private String produtoTecnico;
    private String assuntoProcessoMatriz;
    private String marcaComercial;
    private String dataEncaminhamento;
    private String dataInicioAvaliacao;
    private String dataFimAvaliacao;
    private String nomeProduto;
    private String dataGeracao;
    private String nomeTecnico;

    private List<ExigenciaTecnicaDTO> exigenciaTecnicaDTOs;

    public String getNumero() {
        return ofNullable(numero).orElse(StringUtils.EMPTY);
    }

    public String getNomeEmpresa() {
        return ofNullable(nomeEmpresa).orElse(StringUtils.EMPTY);
    }

    public String getExpediente() {
        return ofNullable(expediente).orElse(StringUtils.EMPTY);
    }

    public String getDataEntrada() {
        return ofNullable(dataEntrada).orElse(StringUtils.EMPTY);
    }

    public String getAssunto() {
        return ofNullable(assunto).orElse(StringUtils.EMPTY);
    }

    public String getNumeroProcesso() {
        return ofNullable(numeroProcesso).orElse(StringUtils.EMPTY);
    }

    public String getNomeComercial() {
        return ofNullable(nomeComercial).orElse(StringUtils.EMPTY);
    }

    public String getNumeroRegistroMAPA() {
        return ofNullable(numeroRegistroMAPA).orElse(StringUtils.EMPTY);
    }

    public String getNumeroProcessoAnvisa() {
        return ofNullable(numeroProcessoAnvisa).orElse(StringUtils.EMPTY);
    }

    public String getProdutoMatriz() {
        return ofNullable(produtoMatriz).orElse(StringUtils.EMPTY);
    }

    public String getTipoFormulacao() {
        return ofNullable(tipoFormulacao).orElse(StringUtils.EMPTY);
    }

    public String getClassificacaoToxicologia() {
        return ofNullable(classificacaoToxicologia).orElse(StringUtils.EMPTY);
    }

    public String getIngredienteAtivo() {
        return ofNullable(ingredienteAtivo).orElse(StringUtils.EMPTY);
    }

    public String getMonografia() {
        return ofNullable(monografia).orElse(StringUtils.EMPTY);
    }

    public String getNivel() {
        return ofNullable(nivel).orElse(StringUtils.EMPTY);
    }

    public String getDataProcolo() {
        return ofNullable(dataProcolo).orElse(StringUtils.EMPTY);
    }

    public String getDescricaoAssunto() {
        return ofNullable(descricaoAssunto).orElse(StringUtils.EMPTY);
    }

    public String getCodigoAssunto() {
        return ofNullable(codigoAssunto).orElse(StringUtils.EMPTY);
    }

    public String getCnpjEmpresa() {
        return ofNullable(cnpjEmpresa).orElse(StringUtils.EMPTY);
    }

    public String getProdutoTecnico() {
        return ofNullable(produtoTecnico).orElse(StringUtils.EMPTY);
    }

    public String getAssuntoProcessoMatriz() {
        return ofNullable(assuntoProcessoMatriz).orElse(StringUtils.EMPTY);
    }

    public String getMarcaComercial() {
        return ofNullable(marcaComercial).orElse(StringUtils.EMPTY);
    }

    public String getDataEncaminhamento() {
        return ofNullable(dataEncaminhamento).orElse(StringUtils.EMPTY);
    }

    public String getDataInicioAvaliacao() {
        return ofNullable(dataInicioAvaliacao).orElse(StringUtils.EMPTY);
    }

    public String getDataFimAvaliacao() {
        return ofNullable(dataFimAvaliacao).orElse(StringUtils.EMPTY);
    }

    public String getNomeProduto() {
        return ofNullable(nomeProduto).orElse(StringUtils.EMPTY);
    }

    public String getDataGeracao() {
        return ofNullable(dataGeracao).orElse(StringUtils.EMPTY);
    }

    public String getNomeTecnico() {
        return ofNullable(nomeTecnico).orElse(StringUtils.EMPTY);
    }

    public List<ExigenciaTecnicaDTO> getExigenciaTecnicaDTOs() {
        return exigenciaTecnicaDTOs;
    }

    public ParecerTecnicoDTO setNumero(String numero) {
        this.numero = numero;
        return this;
    }

    public ParecerTecnicoDTO setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
        return this;
    }

    public ParecerTecnicoDTO setExpediente(String expediente) {
        this.expediente = expediente;
        return this;
    }

    public ParecerTecnicoDTO setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
        return this;
    }

    public ParecerTecnicoDTO setAssunto(String assunto) {
        this.assunto = assunto;
        return this;
    }

    public ParecerTecnicoDTO setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
        return this;
    }

    public ParecerTecnicoDTO setNomeComercial(String nomeComercial) {
        this.nomeComercial = nomeComercial;
        return this;
    }

    public ParecerTecnicoDTO setNumeroRegistroMAPA(String numeroRegistroMAPA) {
        this.numeroRegistroMAPA = numeroRegistroMAPA;
        return this;
    }

    public ParecerTecnicoDTO setNumeroProcessoAnvisa(String numeroProcessoAnvisa) {
        this.numeroProcessoAnvisa = numeroProcessoAnvisa;
        return this;
    }

    public ParecerTecnicoDTO setProdutoMatriz(String produtoMatriz) {
        this.produtoMatriz = produtoMatriz;
        return this;
    }

    public ParecerTecnicoDTO setTipoFormulacao(String tipoFormulacao) {
        this.tipoFormulacao = tipoFormulacao;
        return this;
    }

    public ParecerTecnicoDTO setClassificacaoToxicologia(String classificacaoToxicologia) {
        this.classificacaoToxicologia = classificacaoToxicologia;
        return this;
    }

    public ParecerTecnicoDTO setIngredienteAtivo(String ingredienteAtivo) {
        this.ingredienteAtivo = ingredienteAtivo;
        return this;
    }

    public ParecerTecnicoDTO setMonografia(String monografia) {
        this.monografia = monografia;
        return this;
    }

    public ParecerTecnicoDTO setNivel(String nivel) {
        this.nivel = nivel;
        return this;
    }

    public ParecerTecnicoDTO setDataProcolo(String dataProcolo) {
        this.dataProcolo = dataProcolo;
        return this;
    }

    public ParecerTecnicoDTO setDescricaoAssunto(String descricaoAssunto) {
        this.descricaoAssunto = descricaoAssunto;
        return this;
    }

    public ParecerTecnicoDTO setCodigoAssunto(String codigoAssunto) {
        this.codigoAssunto = codigoAssunto;
        return this;
    }

    public ParecerTecnicoDTO setCnpjEmpresa(String cnpjEmpresa) {
        this.cnpjEmpresa = cnpjEmpresa;
        return this;
    }

    public ParecerTecnicoDTO setProdutoTecnico(String produtoTecnico) {
        this.produtoTecnico = produtoTecnico;
        return this;
    }

    public ParecerTecnicoDTO setAssuntoProcessoMatriz(String assuntoProcessoMatriz) {
        this.assuntoProcessoMatriz = assuntoProcessoMatriz;
        return this;
    }

    public ParecerTecnicoDTO setMarcaComercial(String marcaComercial) {
        this.marcaComercial = marcaComercial;
        return this;
    }

    public ParecerTecnicoDTO setDataEncaminhamento(String dataEncaminhamento) {
        this.dataEncaminhamento = dataEncaminhamento;
        return this;
    }

    public ParecerTecnicoDTO setDataInicioAvaliacao(String dataInicioAvaliacao) {
        this.dataInicioAvaliacao = dataInicioAvaliacao;
        return this;
    }

    public ParecerTecnicoDTO setDataFimAvaliacao(String dataFimAvaliacao) {
        this.dataFimAvaliacao = dataFimAvaliacao;
        return this;
    }

    public ParecerTecnicoDTO setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
        return this;
    }

    public ParecerTecnicoDTO setDataGeracao(String dataGeracao) {
        this.dataGeracao = dataGeracao;
        return this;
    }

    public ParecerTecnicoDTO setNomeTecnico(String nomeTecnico) {
        this.nomeTecnico = nomeTecnico;
        return this;
    }

    public ParecerTecnicoDTO setExigenciaTecnicaDTOs(List<ExigenciaTecnicaDTO> exigenciaTecnicaDTOs) {
        this.exigenciaTecnicaDTOs = exigenciaTecnicaDTOs;
        return this;
    }
}