package br.net.mirante.singular.flow.core.service;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.constraints.NotNull;

import br.net.mirante.singular.commons.base.SingularUtil;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;

public interface IFlowAuthorizationService {

    static final String PATH_PROCESS_INSTANCE_HAS_ACCESS = "/process/instance/has-access";
    static final String PATH_PROCESS_DEFINITION_HAS_ACCESS = "/process/definition/has-access";
    static final String PATH_PROCESS_DEFINITION_WITH_ACCESS = "/process/definition/with-access";
    
    Set<String> listProcessDefinitionsWithAccess(@NotNull String userCod, @NotNull AccessLevel accessLevel);

    boolean hasAccessToProcessDefinition(@NotNull String processDefinitionKey, @NotNull String userCod, @NotNull AccessLevel accessLevel);

    boolean hasAccessToProcessInstance(@NotNull String processInstanceFullId, @NotNull String userCod, @NotNull AccessLevel accessLevel);
    
    static String generateGroupToken(String groupCod){
        return SingularUtil.toSHA1(groupCod+LocalDate.now().toEpochDay());
    }
}
