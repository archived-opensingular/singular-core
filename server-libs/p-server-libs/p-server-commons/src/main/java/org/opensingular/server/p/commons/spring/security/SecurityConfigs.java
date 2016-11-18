package org.opensingular.server.p.commons.spring.security;


import org.opensingular.lib.support.spring.util.AutoScanDisabled;
import org.opensingular.server.commons.config.IServerContext;
import org.opensingular.server.commons.spring.security.config.cas.SingularCASSpringSecurityConfig;
import org.opensingular.server.p.commons.config.PServerContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

public class SecurityConfigs {

    @AutoScanDisabled
    @Configuration
    @EnableWebMvc
    @Order(103)
    public static class CASPeticionamento extends SingularCASSpringSecurityConfig {
        @Override
        protected IServerContext getContext() {
            return PServerContext.PETITION;
        }

        @Override
        public String getCASLogoutURL() {
            return "";
        }
    }

    @AutoScanDisabled
    @Configuration
    @EnableWebMvc
    @Order(104)
    public static class CASAnalise extends SingularCASSpringSecurityConfig {
        @Override
        protected IServerContext getContext() {
            return PServerContext.WORKLIST;
        }

        @Override
        public String getCASLogoutURL() {
            return "";
        }
    }
}
