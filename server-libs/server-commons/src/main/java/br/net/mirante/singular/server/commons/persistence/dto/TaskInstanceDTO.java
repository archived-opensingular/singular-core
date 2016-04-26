package br.net.mirante.singular.server.commons.persistence.dto;


import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.TaskType;

import java.util.Date;

public class TaskInstanceDTO {

    private Integer taskInstanceId;
    private Integer processInstanceId;
    private Integer taskId;
    private String taskName;
    private String numeroProcesso;
    private Date dataProtocolo;
    private String descricao;
    private String solicitante;
    private MUser usuarioAlocado;
    private String nomeUsuarioAlocado;
    private String type;
    private String processType;
    private Long codPeticao;
    private Date situationBeginDate;
    private Date processBeginDate;
    private TaskType taskType;
    private boolean possuiPermissao = false;


    public TaskInstanceDTO(Integer processInstanceId, Integer taskInstanceId, Integer taskId,
                           Date dataProtocolo, String descricao,
                           MUser usuarioAlocado, String taskName, String type, String processType, Long codPeticao,
                           Date situationBeginDate, Date processBeginDate, TaskType taskType, boolean possuiPermissao) {
        this.processInstanceId = processInstanceId;
        this.taskInstanceId = taskInstanceId;
        this.taskId = taskId;
        this.dataProtocolo = dataProtocolo;
        this.descricao = descricao;
//        this.solicitante = nomePessoaFisica == null ? nomePessoaJuridica : nomePessoaFisica;
        this.usuarioAlocado = usuarioAlocado;
        this.nomeUsuarioAlocado = usuarioAlocado == null ? "" : usuarioAlocado.getSimpleName();
        this.taskName = taskName;
        this.codPeticao = codPeticao;
        this.type = type;
        this.processType = processType;
        this.situationBeginDate = situationBeginDate;
        this.processBeginDate = processBeginDate;
        this.taskType = taskType;
        this.possuiPermissao = possuiPermissao;
    }


    public Integer getTaskInstanceId() {
        return taskInstanceId;
    }


    public void setTaskInstanceId(Integer taskInstanceId) {
        this.taskInstanceId = taskInstanceId;
    }


    public Integer getTaskId() {
        return taskId;
    }


    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }


    public String getNumeroProcesso() {
        return numeroProcesso;
    }


    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }


    public Date getDataProtocolo() {
        return dataProtocolo;
    }


    public void setDataProtocolo(Date dataProtocolo) {
        this.dataProtocolo = dataProtocolo;
    }


    public String getDescricao() {
        return descricao;
    }


    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    public String getSolicitante() {
        return solicitante;
    }


    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }


    public MUser getUsuarioAlocado() {
        return usuarioAlocado;
    }


    public void setUsuarioAlocado(MUser usuarioAlocado) {
        this.usuarioAlocado = usuarioAlocado;
    }


    public String getNomeUsuarioAlocado() {
        return nomeUsuarioAlocado;
    }


    public void setNomeUsuarioAlocado(String nomeUsuarioAlocado) {
        this.nomeUsuarioAlocado = nomeUsuarioAlocado;
    }


    public String getTaskName() {
        return taskName;
    }


    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public Long getCodPeticao() {
        return codPeticao;
    }


    public void setCodPeticao(Long codPeticao) {
        this.codPeticao = codPeticao;
    }


    public Integer getProcessInstanceId() {
        return processInstanceId;
    }


    public void setProcessInstanceId(Integer processInstanceId) {
        this.processInstanceId = processInstanceId;
    }


    public Date getSituationBeginDate() {
        return situationBeginDate;
    }


    public void setSituationBeginDate(Date situationBeginDate) {
        this.situationBeginDate = situationBeginDate;
    }


    public Date getProcessBeginDate() {
        return processBeginDate;
    }


    public void setProcessBeginDate(Date processBeginDate) {
        this.processBeginDate = processBeginDate;
    }


    public TaskType getTaskType() {
        return taskType;
    }


    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }


    public boolean isPossuiPermissao() {
        return possuiPermissao;
    }


    public void setPossuiPermissao(boolean possuiPermissao) {
        this.possuiPermissao = possuiPermissao;
    }


    public String getProcessType() {
        return processType;
    }


    public void setProcessType(String processType) {
        this.processType = processType;
    }
}
