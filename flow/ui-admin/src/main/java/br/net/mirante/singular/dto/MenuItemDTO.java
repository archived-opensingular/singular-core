package br.net.mirante.singular.dto;

import java.util.ArrayList;
import java.util.List;

import br.net.mirante.singular.flow.core.dto.IMenuItemDTO;

public class MenuItemDTO implements IMenuItemDTO {
    private static final long serialVersionUID = 25234058060013546L;

    private Long id;
    private String name;
    private String code;
    private Integer counter;
    private List<IMenuItemDTO> itens;

    public MenuItemDTO(Long id, String name, String code, Integer counter) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.counter = counter;
        this.itens = new ArrayList<>();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Integer getCounter() {
        return counter;
    }

    @Override
    public List<IMenuItemDTO> getItens() {
        return itens;
    }

    @Override
    public IMenuItemDTO addItem(IMenuItemDTO item) {
        this.itens.add(item);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MenuItemDTO that = (MenuItemDTO) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
