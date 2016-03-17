/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.core.attachment.handlers;

import java.io.Serializable;
import java.util.UUID;

/**
 * This is the base class for implementing generation strategies for
 *  file ids in the attachment process. 
 *  The default implementation generates a random UUID string.
 * 
 * @author Fabricio Buzeto
 *
 */
@SuppressWarnings("serial")
public class IdGenerator implements Serializable{

    public String generate(byte[] content){
        return UUID.randomUUID().toString();
    }
}
