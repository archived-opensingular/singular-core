package org.opensingular.server.commons.auth;


import org.apache.commons.lang3.StringUtils;
import org.opensingular.server.commons.persistence.entity.parameter.ParameterEntity;
import org.opensingular.server.commons.service.ParameterService;

import javax.inject.Inject;

public class DatabaseAdminCredentialChecker implements AdminCredentialChecker {

    public static final String PARAM_ADMIN_HASH_PASSWORD = "ADMIN_HASH_PASSWORD";
    public static final String PARAM_ADMIN_USERNAME      = "ADMIN_USERNAME";

    private ParameterService parameterService;

    private String codProcessGroup;

    @Inject
    public DatabaseAdminCredentialChecker(ParameterService parameterService, String codProcessGroup) {
        this.parameterService = parameterService;
        this.codProcessGroup = codProcessGroup;
    }

    @Override
    public boolean check(String username, String password) {
        return isPasswordAndUsernameValid(username, password)
                && username.equalsIgnoreCase(retrieveUsername())
                && getSHA1(password).equals(retrievePasswordHash());
    }

    private boolean isPasswordAndUsernameValid(String username, String password) {
        return StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password);
    }

    private String retrievePasswordHash() {
        return retrieveParameter(PARAM_ADMIN_HASH_PASSWORD);
    }

    private String retrieveUsername() {
        return retrieveParameter(PARAM_ADMIN_USERNAME);
    }

    private String retrieveParameter(String parameterName) {
        if (codProcessGroup != null) {
            return parameterService.findByNameAndProcessGroup(parameterName, codProcessGroup)
                    .map(ParameterEntity::getValue)
                    .orElse(null);
        }
        return null;
    }

}