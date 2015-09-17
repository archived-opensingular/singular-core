package br.net.mirante.singular.service;

import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.net.mirante.singular.dao.CategoryMenuDAO;
import br.net.mirante.singular.dao.MenuItemDTO;
import br.net.mirante.singular.flow.core.dto.IMenuItemDTO;

@Service("menuService")
public class MenuServiceImpl implements MenuService {

    @Inject
    private CategoryMenuDAO categoryMenuDAO;

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
        return new ImmutablePair<>((Long) result[0], (Long) result[1]);
    }
}
