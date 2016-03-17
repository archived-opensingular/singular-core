/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.bam.service;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import br.net.mirante.singular.bam.dto.MenuItemDTO;

public interface MenuService {

    List<MenuItemDTO> retrieveAllCategories();

    Pair<Long, Long> retrieveCategoryDefinitionIdsByCode(String code);
    
    List<MenuItemDTO> retrieveAllCategoriesWithAcces(String userId);
}
