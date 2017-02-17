package org.opensingular.server.commons.auth;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class StaticCredentialChecker implements AdminCredentialChecker {

    private static final String USERNAME = "admin";
    private static final String HASH     = "0aca995b93addee9348dcef9016c0f9624dfae3a";

    @Override
    public boolean check(String username, String password) {
        return USERNAME.equalsIgnoreCase(username) && HASH.equals(getSHA1(password));
    }

    private String getSHA1(String value) {
        return Hashing.sha1().hashString(value, StandardCharsets.UTF_8).toString();
    }

}