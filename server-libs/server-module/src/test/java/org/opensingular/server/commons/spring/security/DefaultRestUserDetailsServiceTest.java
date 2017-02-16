package org.opensingular.server.commons.spring.security;

import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.Assert.assertEquals;

public class DefaultRestUserDetailsServiceTest {

    @Test(expected = UsernameNotFoundException.class)
    public void withoutAllowedCommonNameLoadUserByUsernameTest() {
        TestDefaultRestUserDetailsService userDetailsService = new TestDefaultRestUserDetailsService();
        userDetailsService.setAllowedUsername("localhost1212");
        userDetailsService.loadUserByUsername("localhost");
    }

    @Test
    public void withAllowedCommonNameLoadUserByUsernameTest() {
        TestDefaultRestUserDetailsService userDetailsService = new TestDefaultRestUserDetailsService();
        String                            commonName         = "localhost";
        userDetailsService.setAllowedUsername(commonName);
        UserDetails localhostUser = userDetailsService.loadUserByUsername(commonName);
        assertEquals(localhostUser.getUsername(), commonName);
    }

    @Test
    public void createUserDetailsTest() {
        TestDefaultRestUserDetailsService userDetailsService = new TestDefaultRestUserDetailsService();
        String                            commonName         = "localhost";
        UserDetails                       localhostUser      = userDetailsService.createUserDetails(commonName);
        assertEquals(localhostUser.getUsername(), commonName);
    }

    @Test
    public void getSubjectPrincipalRegexTest() {
        TestDefaultRestUserDetailsService userDetailsService = new TestDefaultRestUserDetailsService();
        assertEquals(DefaultRestUserDetailsService.SUBJECT_PRINCIPAL_REGEX, userDetailsService.getSubjectPrincipalRegex());
    }

    private static class TestDefaultRestUserDetailsService extends DefaultRestUserDetailsService {

        private String allowedUsername;

        @Override
        protected String getAllowedCommonName() {
            return allowedUsername;
        }

        void setAllowedUsername(String allowedUsername) {
            this.allowedUsername = allowedUsername;
        }
    }

}
