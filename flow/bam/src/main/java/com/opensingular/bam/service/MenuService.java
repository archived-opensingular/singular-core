/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.service;

import java.util.List;

import com.opensingular.bam.dto.MenuItemDTO;
import org.apache.commons.lang3.tuple.Pair;

public interface MenuService {

    List<MenuItemDTO> retrieveAllCategories();

    Pair<Long, Long> retrieveCategoryDefinitionIdsByCode(String code);
    
    List<MenuItemDTO> retrieveAllCategoriesWithAcces(String userId);
}
