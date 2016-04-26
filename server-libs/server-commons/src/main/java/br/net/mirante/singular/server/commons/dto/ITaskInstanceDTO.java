package br.net.mirante.singular.server.commons.dto;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.TaskType;

import java.io.Serializable;
import java.util.Date;

public interface ITaskInstanceDTO extends Serializable {

    Integer getTaskInstanceId();

    void setTaskInstanceId(Integer taskInstanceId);

    void setVersionStamp(Integer v);

    Integer getVersionStamp();

    Integer getTaskId();

    void setTaskId(Integer taskId);

    String getNumeroProcesso();

    void setNumeroProcesso(String numeroProcesso);

    Date getDataProtocolo();

    void setDataProtocolo(Date dataProtocolo);

    String getDescricao();

    void setDescricao(String descricao);

    String getSolicitante();

    void setSolicitante(String solicitante);

    MUser getUsuarioAlocado();

    void setUsuarioAlocado(MUser usuarioAlocado);

    String getNomeUsuarioAlocado();

    void setNomeUsuarioAlocado(String nomeUsuarioAlocado);

    String getTaskName();

    void setTaskName(String taskName);

    String getType();

    void setType(String type);

    Long getCodPeticao();

    void setCodPeticao(Long codPeticao);

    Integer getProcessInstanceId();

    void setProcessInstanceId(Integer processInstanceId);

    Date getSituationBeginDate();

    void setSituationBeginDate(Date situationBeginDate);

    Date getProcessBeginDate();

    void setProcessBeginDate(Date processBeginDate);

    TaskType getTaskType();

    void setTaskType(TaskType taskType);

    boolean isPossuiPermissao();

    void setPossuiPermissao(boolean possuiPermissao);

    String getProcessType();

    void setProcessType(String processType);
}
