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

package org.opensingular.form.persistence.dao;

import org.apache.commons.io.IOUtils;
import org.opensingular.form.persistence.entity.AttachmentContentEntity;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.io.TempFileInputStream;
import org.opensingular.lib.commons.util.ObjectUtils;
import org.opensingular.lib.support.persistence.BaseDAO;

import javax.annotation.Nonnull;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.function.Consumer;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;

@SuppressWarnings({"serial", "unchecked"})
@Transactional(Transactional.TxType.MANDATORY)
public class AttachmentContentDao<T extends AttachmentContentEntity> extends BaseDAO<T, Long> {

    public AttachmentContentDao() {
        super((Class<T>) AttachmentContentEntity.class);
    }

    protected AttachmentContentDao(Class<T> entityClass) {
        super(entityClass);
    }

    public T insert(T o) {
        getSession().save(o);
        return o;
    }

    public T insert(InputStream is, long length, String hashSha1) {
        if (hashSha1 == null) {
            throw SingularException.rethrow("Essa persistencia de arquivo não suporta o cálculo de hash, favor fornecer o hash calculado.");
        }
        return createContentAndAcceptConsumer(is, length, hashSha1, this::insert);
    }

    public void delete(@Nonnull Long codContent) {
        find(codContent).ifPresent(this::delete);
    }

    /**
     * Cria a entidade e aplica um consumer, que pode ser utilizado para fazer o save.
     * É feito desta forma para garantir o close do TempFileInputStream
     *
     * @param in              o stream
     * @param length          o tamanho
     * @param hashSha1        o hash
     * @param consummerEntity o consummer a ser acionado quando a entidade for criada
     * @return a entidade apos accept do consumer
     */
    protected T createContentAndAcceptConsumer(InputStream in, long length, String hashSha1, Consumer<T> consummerEntity) {
        try {
            File                file  = File.createTempFile(this.getClass().getName(), hashSha1);
            DeflaterInputStream inZip = new DeflaterInputStream(in, new Deflater(Deflater.BEST_COMPRESSION));
            try (FileOutputStream fos = new FileOutputStream(file)) {
                IOUtils.copy(inZip, fos);
            }
            T                   fileEntity     = createInstance();
            /* o TempFileInputStream abaixo não deve ser fechado aqui, fica a cargo do hibernate */
            fileEntity.setContent(getSession().getLobHelper().createBlob(new TempFileInputStream(file), file.length()));//NOSONAR
            fileEntity.setHashSha1(hashSha1);
            fileEntity.setSize(length);
            fileEntity.setInclusionDate(new Date());
            consummerEntity.accept(fileEntity);
            return fileEntity;
        } catch (Exception e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

    @Nonnull
    protected T createInstance() {
        return ObjectUtils.newInstance(entityClass);
    }
}