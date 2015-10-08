package br.net.mirante.singular.flow.core.defaults;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.UserRoleSettingStrategy;

import java.util.Collections;
import java.util.List;

public class EmptyUserRoleSettingStrategy extends UserRoleSettingStrategy<ProcessInstance> {

    @Override
    public List<? extends MUser> listAllocableUsers(ProcessInstance processInstance) {
            return Collections.emptyList();
        }

}