package br.net.mirante.singular.service;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import br.net.mirante.singular.dto.MenuItemDTO;

public interface MenuService {

    List<MenuItemDTO> retrieveAllCategories();

    Pair<Long, Long> retrieveCategoryDefinitionIdsByCode(String code);
    
    List<MenuItemDTO> retrieveAllCategoriesWithAcces(String userId);
}
