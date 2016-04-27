package br.net.mirante.singular.server.commons.persistence.dto;


import java.io.Serializable;
import java.net.URL;
import java.util.Date;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.server.commons.exception.SingularServerException;

public class TaskInstanceDTO implements Serializable {

    private Integer taskInstanceId;
    private Integer versionStamp;
    private Integer processInstanceId;
    private Integer taskId;
    private String taskName;
    private Date creationDate;
    private String descricao;
    private MUser usuarioAlocado;
    private String nomeUsuarioAlocado;
    private String type;
    private String processType;
    private Long codPeticao;
    private Date situationBeginDate;
    private Date processBeginDate;
    private TaskType taskType;
    private boolean possuiPermissao = true;
    private String processGroupCod;
    private String processGroupContext;


    public TaskInstanceDTO(Integer processInstanceId, Integer taskInstanceId, Integer taskId, Integer versionStamp,
                           Date creationDate, String descricao,
                           MUser usuarioAlocado, String taskName, String type, String processType, Long codPeticao,
                           Date situationBeginDate, Date processBeginDate, TaskType taskType, String processGroupCod, String processGroupContext) {
        this.processInstanceId = processInstanceId;
        this.taskInstanceId = taskInstanceId;
        this.taskId = taskId;
        this.versionStamp = versionStamp;
        this.creationDate = creationDate;
        this.descricao = descricao;
        this.usuarioAlocado = usuarioAlocado;
        this.nomeUsuarioAlocado = usuarioAlocado == null ? "" : usuarioAlocado.getSimpleName();
        this.taskName = taskName;
        this.codPeticao = codPeticao;
        this.type = type;
        this.processType = processType;
        this.situationBeginDate = situationBeginDate;
        this.processBeginDate = processBeginDate;
        this.taskType = taskType;
        this.processGroupCod = processGroupCod;
        try {
            final String path = new URL(processGroupContext).getPath();
            this.processGroupContext  = path.substring(0, path.indexOf("/", 1));
        } catch (Exception e) {
            throw new SingularServerException(String.format("Erro ao tentar fazer o parse da URL: %s", processGroupContext), e);
        }
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


    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescricao() {
        return descricao;
    }


    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public Integer getVersionStamp() {
        return versionStamp;
    }

    public void setVersionStamp(Integer versionStamp) {
        this.versionStamp = versionStamp;
    }

    public String getProcessGroupCod() {
        return processGroupCod;
    }

    public void setProcessGroupCod(String processGroupCod) {
        this.processGroupCod = processGroupCod;
    }

    public String getProcessGroupContext() {
        return processGroupContext;
    }

    public void setProcessGroupContext(String processGroupContext) {
        this.processGroupContext = processGroupContext;
    }
}
