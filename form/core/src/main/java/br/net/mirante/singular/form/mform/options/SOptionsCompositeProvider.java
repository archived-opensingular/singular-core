/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.util.transformer.SListBuilder;

/**
 * Interface funcional para prover lambda de montagem de MIlista de MTipoComposto
 * a partir de um ListBuilder
 */
@FunctionalInterface
public interface SOptionsCompositeProvider extends SOptionsProvider {


    /**
     * Returns the list of options for this selection.
     *
     * @param instanceWithOptions : Current instance used to select the options.
     * @return list of options from the expected {@link SInstance} type.
     */
    @Override
    public default SIList<? extends SInstance> listOptions(SInstance instanceWithOptions, String filter) {
        SType<?> tipo;
        if (instanceWithOptions instanceof SIList){
            tipo = ((SIList<?>) instanceWithOptions).getElementsType();
        } else {
            tipo = instanceWithOptions.getType();
        }
        if (!(tipo instanceof STypeComposite)) {
            throw new SingularFormException("Era esperado ser um tipo composto ou uma lista de de tipo de composto", instanceWithOptions);
        }
        SListBuilder lb = new SListBuilder((STypeComposite<?>) tipo);
        listOptions(instanceWithOptions, lb);
        return lb.getList();
    }

    /**
     * Método para montar uma MLista a partir do MListaBuilder
     */
    public void listOptions(SInstance instance, SListBuilder lb);


}
