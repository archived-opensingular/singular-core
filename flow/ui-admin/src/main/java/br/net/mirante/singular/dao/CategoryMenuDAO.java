/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.net.mirante.singular.dto.MenuItemDTO;

@Repository
public class CategoryMenuDAO extends BaseDAO{

    public List<MenuItemDTO> retrieveAll() {
        Map<Integer, MenuItemDTO> categoriesMap = mountCategories(retrieveCategories());
        return categoriesMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private Map<Integer, MenuItemDTO> mountCategories(List<Object[]> rawCategoies) {
        Map<Integer, MenuItemDTO> categories = new HashMap<>();
        for (Object[] category : rawCategoies) {
            MenuItemDTO item = categories.get(category[3]);
            if (item == null) {
                item = new MenuItemDTO((Integer) category[3],
                        (String) category[4],
                        null, null);
                categories.put((Integer) category[3], item);
            }
            item.addItem(new MenuItemDTO((Integer) category[0],
                    (String) category[1],
                    (String) category[2],
                    (Integer) category[5]));
        }
        return categories;
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> retrieveCategories() {
        Query hql = getSession().createQuery("select pd.cod , pd.name, pd.key, cat.cod, cat.name, 0 from ProcessDefinitionEntity pd join pd.category cat");
        return hql.list();
    }

    public Object[] retrieveCategoryDefinitionIdsByCode(String processCode) {
        Query hql = getSession().createQuery("select cat.cod, pd.cod from ProcessDefinitionEntity pd join pd.category cat where pd.key = :processCode");
        hql.setParameter("processCode", processCode);
        return (Object[]) hql.uniqueResult();
    }
}
