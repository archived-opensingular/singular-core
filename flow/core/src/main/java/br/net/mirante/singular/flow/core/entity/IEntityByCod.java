package br.net.mirante.singular.flow.core.entity;

import java.io.Serializable;

public interface IEntityByCod<PK extends Serializable> extends Serializable {

    PK getCod();
}