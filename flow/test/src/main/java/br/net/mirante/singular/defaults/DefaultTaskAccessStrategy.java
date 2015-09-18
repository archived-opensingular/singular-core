package br.net.mirante.singular.defaults;

import br.net.mirante.singular.CoisasQueDeviamSerParametrizadas;
import br.net.mirante.singular.definicao.InstanciaPeticao;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.TaskAccessStrategy;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DefaultTaskAccessStrategy extends TaskAccessStrategy<InstanciaPeticao> {

    @Override
    public boolean canExecute(InstanciaPeticao instance, MUser user) {
        return true;
    }

    @Override
    public Set<Serializable> getFirstLevelUsersCodWithAccess(InstanciaPeticao instancia) {
        return Collections.emptySet();
    }

    @Override
    public List<? extends MUser> listAllocableUsers(InstanciaPeticao instancia) {
        return Collections.singletonList(CoisasQueDeviamSerParametrizadas.USER);
    }

    @Override
    public List<String> getExecuteRoleNames(ProcessDefinition<?> definicao, MTask<?> task) {
        return Collections.singletonList("ANALISTA");
    }

}
