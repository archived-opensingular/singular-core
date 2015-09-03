package br.net.mirante.singular.flow.core.entity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public interface IEntityTaskDefinition extends IEntityByCod {

    IEntityProcessDefinition getProcessDefinition();

    String getAbbreviation();
    
    List<? extends IEntityTask> getVersions();

    default IEntityTask getLastVersion(){
        return getVersions().stream().collect(Collectors.maxBy(Comparator.comparing(IEntityTask::getVersionDate))).get();
    }
}
