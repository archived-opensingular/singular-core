package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.dto;

import java.io.Serializable;
import java.util.List;

public class ParecerTecnicoDTO implements Serializable {

    private String numero;
    private String nomeEmpresa;
    private String expediente;
    private String dataEntrada;
    private String numeroProcesso;
    private String nomeComercial;
    private String numeroRegistroMAPA;
    private String monografia;
    private String nivel;
    private String dataProcolo;
    private String codigoAssunto;
    private String descricaoAssunto;
    private String cnpjEmpresa;
    private String dataEncaminhamento;
    private String dataInicioAvaliacao;
    private String nomeProduto;
    private String dataGeracao;
    private String nomeTecnico;
    private String siglaTipoFormulacao;
    private String nomeTipoFormulacao;

    private List<String>              produtosTecnicos;
    private List<String>              numerosProcessoProdutosTecnicos;
    private List<String>              ingredientesAtivos;
    private List<ExigenciaTecnicaDTO> exigencias;

    public String getNumero() {
        return numero;
    }

    public ParecerTecnicoDTO setNumero(String numero) {
        this.numero = numero;
        return this;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public ParecerTecnicoDTO setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
        return this;
    }

    public String getExpediente() {
        return expediente;
    }

    public ParecerTecnicoDTO setExpediente(String expediente) {
        this.expediente = expediente;
        return this;
    }

    public String getDataEntrada() {
        return dataEntrada;
    }

    public ParecerTecnicoDTO setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
        return this;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public ParecerTecnicoDTO setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
        return this;
    }

    public String getNomeComercial() {
        return nomeComercial;
    }

    public ParecerTecnicoDTO setNomeComercial(String nomeComercial) {
        this.nomeComercial = nomeComercial;
        return this;
    }

    public String getNumeroRegistroMAPA() {
        return numeroRegistroMAPA;
    }

    public ParecerTecnicoDTO setNumeroRegistroMAPA(String numeroRegistroMAPA) {
        this.numeroRegistroMAPA = numeroRegistroMAPA;
        return this;
    }

    public String getMonografia() {
        return monografia;
    }

    public ParecerTecnicoDTO setMonografia(String monografia) {
        this.monografia = monografia;
        return this;
    }

    public String getNivel() {
        return nivel;
    }

    public ParecerTecnicoDTO setNivel(String nivel) {
        this.nivel = nivel;
        return this;
    }

    public String getDataProcolo() {
        return dataProcolo;
    }

    public ParecerTecnicoDTO setDataProcolo(String dataProcolo) {
        this.dataProcolo = dataProcolo;
        return this;
    }

    public String getDescricaoAssunto() {
        return descricaoAssunto;
    }

    public ParecerTecnicoDTO setDescricaoAssunto(String descricaoAssunto) {
        this.descricaoAssunto = descricaoAssunto;
        return this;
    }

    public String getCodigoAssunto() {
        return codigoAssunto;
    }

    public ParecerTecnicoDTO setCodigoAssunto(String codigoAssunto) {
        this.codigoAssunto = codigoAssunto;
        return this;
    }

    public String getCnpjEmpresa() {
        return cnpjEmpresa;
    }

    public ParecerTecnicoDTO setCnpjEmpresa(String cnpjEmpresa) {
        this.cnpjEmpresa = cnpjEmpresa;
        return this;
    }

    public List<String> getProdutosTecnicos() {
        return produtosTecnicos;
    }

    public ParecerTecnicoDTO setProdutosTecnicos(List<String> produtosTecnicos) {
        this.produtosTecnicos = produtosTecnicos;
        return this;
    }

    public String getDataEncaminhamento() {
        return dataEncaminhamento;
    }

    public ParecerTecnicoDTO setDataEncaminhamento(String dataEncaminhamento) {
        this.dataEncaminhamento = dataEncaminhamento;
        return this;
    }

    public String getDataInicioAvaliacao() {
        return dataInicioAvaliacao;
    }

    public ParecerTecnicoDTO setDataInicioAvaliacao(String dataInicioAvaliacao) {
        this.dataInicioAvaliacao = dataInicioAvaliacao;
        return this;
    }


    public String getNomeProduto() {
        return nomeProduto;
    }

    public ParecerTecnicoDTO setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
        return this;
    }

    public String getDataGeracao() {
        return dataGeracao;
    }

    public ParecerTecnicoDTO setDataGeracao(String dataGeracao) {
        this.dataGeracao = dataGeracao;
        return this;
    }

    public String getNomeTecnico() {
        return nomeTecnico;
    }

    public ParecerTecnicoDTO setNomeTecnico(String nomeTecnico) {
        this.nomeTecnico = nomeTecnico;
        return this;
    }

    public String getSiglaTipoFormulacao() {
        return siglaTipoFormulacao;
    }

    public ParecerTecnicoDTO setSiglaTipoFormulacao(String siglaTipoFormulacao) {
        this.siglaTipoFormulacao = siglaTipoFormulacao;
        return this;
    }

    public String getNomeTipoFormulacao() {
        return nomeTipoFormulacao;
    }

    public ParecerTecnicoDTO setNomeTipoFormulacao(String nomeTipoFormulacao) {
        this.nomeTipoFormulacao = nomeTipoFormulacao;
        return this;
    }

    public List<String> getIngredientesAtivos() {
        return ingredientesAtivos;
    }

    public ParecerTecnicoDTO setIngredientesAtivos(List<String> ingredientesAtivos) {
        this.ingredientesAtivos = ingredientesAtivos;
        return this;
    }

    public List<ExigenciaTecnicaDTO> getExigencias() {
        return exigencias;
    }

    public ParecerTecnicoDTO setExigencias(List<ExigenciaTecnicaDTO> exigencias) {
        this.exigencias = exigencias;
        return this;
    }

    public List<String> getNumerosProcessoProdutosTecnicos() {
        return numerosProcessoProdutosTecnicos;
    }

    public ParecerTecnicoDTO setNumerosProcessoProdutosTecnicos(List<String> numerosProcessoProdutosTecnicos) {
        this.numerosProcessoProdutosTecnicos = numerosProcessoProdutosTecnicos;
        return this;
    }
}