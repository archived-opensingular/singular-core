package br.net.mirante.singular.flow.core.entity;

import java.util.Date;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityRole extends IEntityByCod {
    
    IEntityProcessRole getRole();

    MUser getUser();

    Date getCreateDate();

    MUser getAllocatorUser();

    IEntityProcessInstance getProcessInstance();

}
