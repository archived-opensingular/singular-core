package br.net.mirante.singular.dao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryMenuDAO {

    @Inject
    private SessionFactory sessionFactory;

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public List<MenuItemDTO> retrieveAll() {
        List<MenuItemDTO> categories = new ArrayList<>();
        categories.add(new MenuItemDTO(1L, "Categoria 1", "#", null)
                .addItem(new MenuItemDTO(1L, "Definição X", "#", 1))
                .addItem(new MenuItemDTO(2L, "Definição Y", "#", 2)));
        categories.add(new MenuItemDTO(2L, "Categoria 2", "#", null)
                .addItem(new MenuItemDTO(3L, "Definição Z", "#", 0)));
        return categories;
    }
}
