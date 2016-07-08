/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.net.mirante.singular.server.commons.persistence.entity.form.Petition;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.support.spring.util.AutoScanDisabled;

@AutoScanDisabled
@RequestMapping("/rest/flow")
@RestController
public class DefaultServerREST {

    static final Logger LOGGER = LoggerFactory.getLogger(DefaultServerREST.class);

    public static final String PATH_BOX_ACTION = "/box/action";
    public static final String DELETE = "/delete";

    @Inject
    protected PetitionService<Petition> petitionService;

    @RequestMapping(value = PATH_BOX_ACTION + DELETE, method = RequestMethod.POST)
    public Boolean excluir(@RequestBody Long id) {
        try {
            petitionService.delete(id);
        } catch (Exception e) {
            LOGGER.error("Erro ao excluir o item.", e);
            return false;
        }

        return true;
    }

}