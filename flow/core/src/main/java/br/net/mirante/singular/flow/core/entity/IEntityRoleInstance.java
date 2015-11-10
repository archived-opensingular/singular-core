package br.net.mirante.singular.flow.core.entity;

import java.util.Date;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityRoleInstance extends IEntityByCod<Integer> {

    IEntityRoleDefinition getRole();

    MUser getUser();

    Date getCreateDate();

    MUser getAllocatorUser();

    IEntityProcessInstance getProcessInstance();

}
