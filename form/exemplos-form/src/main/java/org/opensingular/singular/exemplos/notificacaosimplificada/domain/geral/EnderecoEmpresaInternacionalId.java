/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.exemplos.notificacaosimplificada.domain.geral;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "endereco-empresa-internacional-id")
@XmlRootElement(name = "endereco-empresa-internacional-id", namespace = "http://www.anvisa.gov.br/geral/schema/domains")
@Embeddable
public class EnderecoEmpresaInternacionalId implements Serializable {

    private static final long serialVersionUID = 1L;

    @JoinColumn(name = "CO_EMPRESA_INTERNACIONAL")
    @ManyToOne
    private EmpresaInternacional empresaInternacional;
   
    @Column(name = "NU_SEQ_ENDERECO", nullable = false, precision = 4, scale = 0)
    private Short sequencialEndereco;

    public EmpresaInternacional getEmpresaInternacional() {
        return empresaInternacional;
    }

    public void setEmpresaInternacional(EmpresaInternacional empresaInternacional) {
        this.empresaInternacional = empresaInternacional;
    }

    public Short getSequencialEndereco() {
        return sequencialEndereco;
    }

    public void setSequencialEndereco(Short sequencialEndereco) {
        this.sequencialEndereco = sequencialEndereco;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime
                * result
                + ((empresaInternacional == null) ? 0 : empresaInternacional
                        .hashCode());
        result = prime
                * result
                + ((sequencialEndereco == null) ? 0 : sequencialEndereco
                        .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        EnderecoEmpresaInternacionalId other = (EnderecoEmpresaInternacionalId) obj;
        if (empresaInternacional == null) {
            if (other.empresaInternacional != null){
                return false;
            }
        } else if (!empresaInternacional.equals(other.empresaInternacional)){
            return false;
        }
        if (sequencialEndereco == null) {
            if (other.sequencialEndereco != null){
                return false;
            }
        } else if (!sequencialEndereco.equals(other.sequencialEndereco)){
            return false;
        }
        return true;
    }
}
