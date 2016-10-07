/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.util.transformer;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeComposite;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária para montar um MILista de MIComposto
 */
public class SCompositeListBuilder {

    private List<SIComposite>           list;
    private STypeComposite<SIComposite> type;
    private SInstance                   currentInstance;

    /**
     * Instancia do tipo dos elementos da lista
     *
     * @param type
     */
    public SCompositeListBuilder(STypeComposite<SIComposite> type, SInstance currentInstance) {
        this.type = type;
        this.list = new ArrayList<>();
        this.currentInstance = currentInstance;
    }

    /**
     * Cria uma nova instancia do MTipo T na lista
     *
     * @return
     */
    public SCompositeValueSetter add() {
        SIComposite newInstance = type.newInstance();
        list.add(newInstance);
        return new SCompositeValueSetter(newInstance, this);
    }

    public List<SIComposite> getList() {
        return list;
    }

    public SInstance getCurrentInstance() {
        return currentInstance;
    }

    public static class SCompositeValueSetter {

        private SCompositeListBuilder _lb;
        private SIComposite           instancia;

        SCompositeValueSetter(SIComposite instancia, SCompositeListBuilder lb) {
            this._lb = lb;
            this.instancia = instancia;
        }

        public SCompositeValueSetter set(SType<?> tipo, Object value) {
            instancia.setValue(tipo, value);
            return this;
        }

        public SCompositeValueSetter set(String path, Object value) {
            instancia.setValue(path, value);
            return this;
        }

        public SCompositeValueSetter add() {
            return _lb.add();
        }

        public List<SIComposite> getList() {
            return _lb.getList();
        }
    }

}
