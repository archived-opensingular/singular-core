package br.net.mirante.singular.server.commons.flow;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
import br.net.mirante.singular.server.commons.service.IServerMetadataREST;
import br.net.mirante.singular.server.commons.service.dto.MenuGroupDTO;
import br.net.mirante.singular.server.commons.service.dto.ProcessDTO;
import br.net.mirante.singular.support.spring.util.AutoScanDisabled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoScanDisabled
@RequestMapping("/rest/flow")
@RestController
public class DefaultServerMetadataREST implements IServerMetadataREST {

    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    @RequestMapping(value = PATH_LIST_MENU, method = RequestMethod.GET)
    @Override
    public List<MenuGroupDTO> listMenu() {

        List<MenuGroupDTO> groupDTOs = new ArrayList<>();
        Map<String, List<ProcessDefinition>> definitionMap = new HashMap<>();
        Flow.getDefinitions().forEach(d -> {
            if (!definitionMap.containsKey(d.getCategory())) {
                definitionMap.put(d.getCategory(), new ArrayList<>());
            }
            definitionMap.get(d.getCategory()).add(d);

        });

        definitionMap.forEach((category, definitions) -> {
            MenuGroupDTO menuGroupDTO = new MenuGroupDTO();
            menuGroupDTO.setLabel(category);
            menuGroupDTO.setProcesses(new ArrayList<>());
            definitions.forEach(d ->
                            menuGroupDTO
                                    .getProcesses()
                                    .add(
                                            new ProcessDTO(d.getKey(), d.getName(), singularServerConfiguration.processDefinitionFormNameMap().get(d.getClass()))
                                    )
            );
            groupDTOs.add(menuGroupDTO);
        });


        return groupDTOs;
    }


}