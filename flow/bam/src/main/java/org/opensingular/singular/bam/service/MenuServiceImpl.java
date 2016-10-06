/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.bam.service;

import org.opensingular.singular.bam.dao.CategoryMenuDAO;
import org.opensingular.singular.bam.dao.GroupDAO;
import org.opensingular.singular.bam.dto.MenuItemDTO;
import org.opensingular.singular.flow.core.authorization.AccessLevel;
import org.opensingular.singular.flow.core.dto.GroupDTO;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("menuService")
public class MenuServiceImpl implements MenuService {

    @Inject
    private CategoryMenuDAO categoryMenuDAO;

    @Inject
    private GroupDAO groupDAO;

    @Inject
    private FlowMetadataFacade flowMetadataFacade;

    @Override
    @Transactional
    @Cacheable(value = "retrieveAllCategoriesMenu", cacheManager = "cacheManager")
    public List<MenuItemDTO> retrieveAllCategories() {
        return categoryMenuDAO.retrieveAll();
    }

    @Override
    @Transactional
    @Cacheable(value = "retrieveCategoryDefinitionIdsByCodeMenu", key = "#code", cacheManager = "cacheManager")
    public Pair<Long, Long> retrieveCategoryDefinitionIdsByCode(String code) {
        Object[] result = categoryMenuDAO.retrieveCategoryDefinitionIdsByCode(code);
        return new ImmutablePair<>(((Integer) result[0]).longValue(), ((Integer) result[1]).longValue());
    }

    @Override
    @Transactional
    @Cacheable(value = "retrieveAllCategoriesWithAccessMenu", key = "#userId", cacheManager = "cacheManager")
    public List<MenuItemDTO> retrieveAllCategoriesWithAcces(String userId) {
        Set<Integer> definitions = new HashSet<>();
        for (GroupDTO groupDTO : groupDAO.retrieveAll()) {
            definitions.addAll(flowMetadataFacade.listProcessDefinitionCodsWithAccess(groupDTO, userId, AccessLevel.LIST));
        }
        List<MenuItemDTO> allCategories = retrieveAllCategories();

        for (MenuItemDTO category : allCategories) {
            category.getItens().removeIf(def -> {
                return !definitions.contains(def.getId());
            });
        }

        allCategories.removeIf(category -> category.getItens().isEmpty());
        return allCategories;
    }
}
