package br.net.mirante.singular.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.net.mirante.singular.dao.CategoryMenuDAO;
import br.net.mirante.singular.dao.GroupDAO;
import br.net.mirante.singular.dto.DefinitionDTO;
import br.net.mirante.singular.dto.GroupDTO;
import br.net.mirante.singular.dto.MenuItemDTO;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;

@Service("menuService")
public class MenuServiceImpl implements MenuService {

    @Inject
    private CategoryMenuDAO categoryMenuDAO;

    @Inject
    private GroupDAO groupDAO;

    @Inject
    private FlowAuthorizationFacade authorizationFacade;
    
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
    
    @Override
    @Transactional
    @Cacheable(value = "retrieveAllCategoriesWithAccessMenu", key = "#userId", cacheManager = "cacheManager")
    public List<MenuItemDTO> retrieveAllCategoriesWithAcces(String userId) {
        Set<Long> definitions = new HashSet<>();
        for (GroupDTO groupDTO : groupDAO.retrieveAll()) {
            definitions.addAll(authorizationFacade.listProcessDefinitionsWithAccess(groupDTO, userId, AccessLevel.LIST).stream().map(DefinitionDTO::getCod).collect(Collectors.toList()));
        }
        List<MenuItemDTO> allCategories = retrieveAllCategories();
        allCategories.forEach(category ->{
            category.getItens().removeIf(def -> !definitions.contains(def.getId()));
        });
        allCategories.removeIf(category -> category.getItens().isEmpty());
        return allCategories;
    }
}
