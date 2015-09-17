package br.net.mirante.singular.service;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import br.net.mirante.singular.flow.core.dto.IMenuItemDTO;

public interface MenuService {

    List<IMenuItemDTO> retrieveAllCategories();

    Pair<Long, Long> retrieveCategoryDefinitionIdsByCode(String code);
}
