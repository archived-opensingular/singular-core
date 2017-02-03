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

package org.opensingular.server.core.service;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.opensingular.flow.schedule.IScheduleData;
import org.opensingular.flow.schedule.IScheduledJob;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.persistence.entity.AttachmentContentEntity;
import org.opensingular.form.persistence.entity.AttachmentEntity;
import org.opensingular.form.persistence.service.AttachmentPersistenceService;
import org.opensingular.lib.commons.util.Loggable;

/**
 * Job responsável por fazer a coleta de lixo da tabela de anexos
 * do singular, removendo os arquivos que não tenha sido vinculados
 * a nada em um prazo posterior a 24 horas da sua inclusão.
 *
 * Isso pode acontecer quando, por exemplo, um usuário faz um upload
 * e sai do formulário descartando suas alterações.
 */
public class AttachmentGCJob implements IScheduledJob, Loggable {

    @Inject @Named(SDocument.FILE_PERSISTENCE_SERVICE)
    private AttachmentPersistenceService<AttachmentEntity, AttachmentContentEntity> persistenceHandler;

    private IScheduleData scheduleData;

    public AttachmentGCJob(IScheduleData scheduleData) {
        super();
        this.scheduleData = scheduleData;
    }

    @Override
    public IScheduleData getScheduleData() {
        return scheduleData;
    }

    @Override
    public Object run() {

        List<AttachmentEntity> orphanAttachments = persistenceHandler.listOldOrphanAttachments();
        long failed = 0;

        for (AttachmentEntity attachment : orphanAttachments) {
            try {
                persistenceHandler.deleteAttachmentAndContent(attachment);
            } catch (Exception e) {
                getLogger().error(String.format("Failed to delete attachment with id: %s", attachment.getCod()), e);
                failed++;
            }
        }
        String msg = String.format("Removed %d old orphan attachments from %d total.", orphanAttachments.size() - failed, orphanAttachments.size());
        getLogger().info(msg);
        return msg;

    }

    @Override
    public String getId() {
        return "AttachmentGC";
    }

    @Override
    public String toString() {
        return "AttachmentGCJob [getScheduleData()=" + getScheduleData() + ", getId()=" + getId() + "]";
    }

}
