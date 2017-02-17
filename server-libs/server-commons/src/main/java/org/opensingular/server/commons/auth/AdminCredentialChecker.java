package org.opensingular.server.commons.auth;


public interface AdminCredentialChecker {

    boolean check(String username, String password);

}