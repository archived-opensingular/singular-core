/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.crud.services;

import javax.inject.Inject;

import java.util.List;

import org.springframework.stereotype.Component;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.showcase.dao.form.ExampleFile;
import br.net.mirante.singular.showcase.dao.form.FileDao;

@SuppressWarnings("serial")
@Component("filesChoiceProvider")
public class MFileIdsOptionsProvider implements SOptionsProvider {
    @Inject
    private FileDao filePersistence;

    /**
     * Returns a MILista of the type of the field it will be used.
     *
     * @param optionsInstance : Current instance of the selection.
     */
    @Override
    // @destacar:bloco
    public SIList<? extends SInstance> listOptions(SInstance optionsInstance, String filter) {
        SIList<?> list;
        if (optionsInstance instanceof SIList) {
            list = ((SIList) optionsInstance).getElementsType().newList();
        } else {
            list = optionsInstance.getType().newList();
        }
        files().forEach(f -> list.addValue(f.getId()));
        return list;
    }
    // @destacar:fim

    private List<ExampleFile> files() {
        return filePersistence.list();
    }
}