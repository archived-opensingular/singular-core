/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bamclient.exeptions;

public class BamClientExeption extends RuntimeException {

    public BamClientExeption(String message) {
        super(message);
    }
}