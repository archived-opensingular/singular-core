package br.net.mirante.singular.definicao.role.strategy;

import br.net.mirante.singular.definicao.InstanciaPeticao;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.UserRoleSettingStrategy;

import java.util.Collections;
import java.util.List;

public class EmptyUserRoleSettingStrategy extends UserRoleSettingStrategy<InstanciaPeticao> {

    @Override
    public List<? extends MUser> listAllocableUsers(InstanciaPeticao instancia) {
            return Collections.emptyList();
        }

}