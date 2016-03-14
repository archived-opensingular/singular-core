/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeSimple;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface SSelectionableCompositeType<BASE extends SType> extends SSelectionableType<BASE> {

    /**
     * Monta um campo de seleção a partir de um tipo composto utilizando
     * um provider de MILista de MTipoComposto e utiliza o MTipoSimples label filho imediato do
     * tipo composto como label
     *
     * @param label    MTipoSimples filho do composto que será utilizado como label.
     * @param provider
     * @return
     */
    default public BASE withSelectionFromProvider(STypeSimple label, SOptionsCompositeProvider provider) {
        this.setSelectLabel(label.getNameSimple());
        this.setOptionsProvider(provider);
        return (BASE) this;
    }

    /**
     * Monta um campo de seleção a partir de um tipo composto utilizando
     * um provider de MILista de MTipoComposto e utiliza o pathtoLabel como caminho para um MTipoSimples filho imediato do
     * tipo composto para identificar o label
     *
     * @param pathTolabel
     * @param provider
     * @return
     */
    default public BASE withSelectionFromProvider(String pathTolabel, SOptionsCompositeProvider provider) {
        this.setSelectLabel(pathTolabel);
        this.setOptionsProvider(provider);
        return (BASE) this;
    }

    default public BASE withSelectionFromProvider(STypeSimple label, SOptionsProvider provider) {
        this.setSelectLabel(label.getNameSimple());
        this.setOptionsProvider(provider);
        return (BASE) this;
    }

    default public BASE withSelectionFromProvider(String pathTolabel, SOptionsProvider provider) {
        this.setSelectLabel(pathTolabel);
        this.setOptionsProvider(provider);
        return (BASE) this;
    }

}
