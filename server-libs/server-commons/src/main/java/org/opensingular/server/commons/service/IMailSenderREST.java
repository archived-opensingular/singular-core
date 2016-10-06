/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.server.commons.service;

import org.opensingular.lib.commons.util.Loggable;

public interface IMailSenderREST extends Loggable {

    public static final String PATH = "/rest/mail";
    public static final String PATH_SEND_ALL = "/sendAll";
    
    /**
     * Aciona o job para envio dos emails psersistidos em banco
     */
    boolean sendAll();
}
