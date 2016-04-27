/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.crud.services;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.provider.SimpleProvider;
import br.net.mirante.singular.showcase.dao.form.ExampleFile;
import br.net.mirante.singular.showcase.dao.form.FileDao;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@SuppressWarnings("serial")
@Component("filesChoiceProvider")
public class MFileIdsOptionsProvider implements SimpleProvider<ExampleFile> {

    @Inject
    private FileDao filePersistence;

    @Override
    public List<ExampleFile> load(SInstance ins) {
        return filePersistence.list();
    }

}