package org.opensingular.server.commons.auth;


import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public interface AdminCredentialChecker {

    boolean check(String username, String password);

    default String getSHA1(String value) {
        return Hashing.sha1().hashString(value, StandardCharsets.UTF_8).toString();
    }

}