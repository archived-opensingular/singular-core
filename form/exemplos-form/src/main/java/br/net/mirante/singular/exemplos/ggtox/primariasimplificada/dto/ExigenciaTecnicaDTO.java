package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.dto;

public class ExigenciaTecnicaDTO {

    private String assunto;
    private String expedienteExigencia;
    private String expedienteResposta;
    private String dataEnvio;
    private String dataResposta;

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getExpedienteExigencia() {
        return expedienteExigencia;
    }

    public void setExpedienteExigencia(String expedienteExigencia) {
        this.expedienteExigencia = expedienteExigencia;
    }

    public String getExpedienteResposta() {
        return expedienteResposta;
    }

    public void setExpedienteResposta(String expedienteResposta) {
        this.expedienteResposta = expedienteResposta;
    }

    public String getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(String dataEnvio) {
        this.dataEnvio = dataEnvio;
    }

    public String getDataResposta() {
        return dataResposta;
    }

    public void setDataResposta(String dataResposta) {
        this.dataResposta = dataResposta;
    }
}
