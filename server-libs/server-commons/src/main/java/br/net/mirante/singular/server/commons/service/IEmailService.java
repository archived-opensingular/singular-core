/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.server.commons.service;

import br.net.mirante.singular.server.commons.service.dto.Email;

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
