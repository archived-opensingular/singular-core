/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.form.service;

import java.io.Serializable;

/**
 * DTO representing a Form with id
 */
public class FormDTO implements Serializable{

    private Long cod;

    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cod == null) ? 0 : cod.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FormDTO other = (FormDTO) obj;
        if (cod == null) {
            if (other.cod != null)
                return false;
        } else if (!cod.equals(other.cod))
            return false;
        return true;
    }
    
}
