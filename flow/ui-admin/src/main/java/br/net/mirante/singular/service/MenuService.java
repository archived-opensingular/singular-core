package br.net.mirante.singular.service;

import java.util.List;

import br.net.mirante.singular.dao.MenuItemDTO;

public interface MenuService {

    List<MenuItemDTO> retrieveAllCategories();
}
