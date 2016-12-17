/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.persistence.dao.form;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.opensingular.flow.persistence.entity.ProcessDefinitionEntity;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.lib.support.persistence.BaseDAO;
import org.opensingular.server.commons.persistence.dto.PetitionHistoryDTO;
import org.opensingular.server.commons.persistence.entity.form.FormVersionHistoryEntity;
import org.opensingular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.server.commons.service.dto.MenuGroup;
import org.opensingular.server.commons.service.dto.ProcessDTO;
import org.opensingular.server.commons.wicket.SingularSession;
import org.opensingular.server.commons.wicket.view.template.MenuSessionConfig;

public class PetitionContentHistoryDAO extends BaseDAO<PetitionContentHistoryEntity, Long> {

    public PetitionContentHistoryDAO() {
        super(PetitionContentHistoryEntity.class);
    }

    public List<PetitionHistoryDTO> listPetitionContentHistoryByPetitionCod(PetitionEntity petitionEntity, String menu, boolean filter) {

        final List<TaskInstanceEntity>           tasks;
        final List<PetitionContentHistoryEntity> histories;
        final List<PetitionHistoryDTO>           petitionHistoryDTOs;
        final List<Integer>                      petitionHistoryTaskCods;

        tasks = getSession()
                .createQuery("select task from PetitionEntity p " +
                        " inner join p.processInstanceEntity.tasks as task where p.cod = :petitionCod")
                .setParameter("petitionCod", petitionEntity.getCod()).list();

        histories = getSession()
                .createQuery("select h from PetitionContentHistoryEntity h " +
                        " where h.petitionEntity.cod = :petitionCod")
                .setParameter("petitionCod", petitionEntity.getCod()).list();

        petitionHistoryDTOs = new ArrayList<>();

        histories.forEach(history -> {
            petitionHistoryDTOs.add(new PetitionHistoryDTO().setPetitionContentHistory(history).setTask(history.getTaskInstanceEntity()));
        });

        petitionHistoryTaskCods = histories
                .stream()
                .map(PetitionContentHistoryEntity::getTaskInstanceEntity)
                .map(TaskInstanceEntity::getCod)
                .collect(Collectors.toList());

        tasks
                .stream()
                .filter(task -> !petitionHistoryTaskCods.contains(task.getCod()))
                .forEach(task -> {
                    petitionHistoryDTOs.add(new PetitionHistoryDTO().setTask(task));
                });

        MenuSessionConfig menuSessionConfig = SingularSession.get().getMenuSessionConfig();
        MenuGroup         menuGroup         = menuSessionConfig.getMenuPorLabel(menu);

        return petitionHistoryDTOs
                .stream()
                .filter(p -> filterAllowedHistoryTasks(p, menuGroup, filter))
                .sorted((a,b) -> a.getTask().getBeginDate().compareTo(b.getTask().getBeginDate()))
                .collect(Collectors.toList());

    }

    private boolean filterAllowedHistoryTasks(PetitionHistoryDTO petitionHistoryDTO, MenuGroup menuGroup, boolean filter) {
        if (!filter) {
            return true;
        }

        ProcessDefinitionEntity processDefinition     = petitionHistoryDTO.getTask().getProcessInstance().getProcessVersion().getProcessDefinition();
        ProcessDTO              processByAbbreviation = menuGroup.getProcessByAbbreviation(processDefinition.getKey());
        return processByAbbreviation != null
                && processByAbbreviation.getAllowedHistoryTasks().contains(petitionHistoryDTO.getTask().getTask().getAbbreviation());
    }

    public FormVersionHistoryEntity findLastestByPetitionCodAndType(String typeName, Long cod) {
        return (FormVersionHistoryEntity) getSession().createQuery(" select fvhe from PetitionContentHistoryEntity p " +
                " inner join p.formVersionHistoryEntities  fvhe " +
                " inner join fvhe.formVersion fv  " +
                " inner join fv.formEntity fe  " +
                " inner join fe.formType ft  " +
                " where ft.abbreviation = :typeName and p.petitionEntity.cod = :cod " +
                " order by p.historyDate desc ")
                .setParameter("typeName", typeName)
                .setParameter("cod", cod)
                .setMaxResults(1)
                .uniqueResult();
    }
}