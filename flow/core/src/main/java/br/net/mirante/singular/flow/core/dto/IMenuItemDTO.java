package br.net.mirante.singular.flow.core.dto;

import java.io.Serializable;
import java.util.List;

public interface IMenuItemDTO extends Serializable {
    Long getId();

    String getName();

    String getCode();

    Integer getCounter();

    List<IMenuItemDTO> getItens();

    IMenuItemDTO addItem(IMenuItemDTO item);
}
