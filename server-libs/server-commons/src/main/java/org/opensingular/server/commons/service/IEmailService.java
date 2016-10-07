/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.server.commons.service;

import org.opensingular.server.commons.service.dto.Email;

/**
 * Serviço de envio de e-mail
 * 
 * @author lucas.lopes
 */
public interface IEmailService<X extends Email> {

    boolean send(X email);
    
    @SuppressWarnings("unchecked")
    default X createEmail(String subject) {
        return (X) new Email().withSubject(subject);
    }
}
