package br.net.mirante.singular.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDTO implements Serializable {
    private static final long serialVersionUID = 25234058060013546L;

    private Long id;
    private String label;
    private String href;
    private Integer counter;
    private List<MenuItemDTO> itens;

    public MenuItemDTO(Long id, String label, String href, Integer counter) {
        this.id = id;
        this.label = label;
        this.href = href;
        this.counter = counter;
        this.itens = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getHref() {
        return href;
    }

    public Integer getCounter() {
        return counter;
    }

    public List<MenuItemDTO> getItens() {
        return itens;
    }

    public MenuItemDTO addItem(MenuItemDTO item) {
        this.itens.add(item);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MenuItemDTO that = (MenuItemDTO) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
