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

import org.opensingular.form.persistence.entity.AttachmentContentEntity;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.support.persistence.BaseDAO;

import javax.annotation.Nonnull;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.util.Date;
import java.util.Optional;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;

@SuppressWarnings({"serial", "unchecked"})
@Transactional(Transactional.TxType.MANDATORY)
public class AttachmentContentDao<T extends AttachmentContentEntity> extends BaseDAO<T, Long> {

    public AttachmentContentDao() {
        super((Class<T>) AttachmentContentEntity.class);
    }

    protected AttachmentContentDao(Class<T> tipo) {
        super(tipo);
    }

    public T insert(T o) {
        getSession().save(o);
        return o;
    }

    public T insert(InputStream is, long length, String hashSha1) {
        if (hashSha1 == null){
            throw SingularException.rethrow("Essa persistencia de arquivo não suporta o cálculo de hash, favor fornecer o hash calculado.");
        }
        return insert(createContent(is, length, hashSha1));
    }

    public void delete(@Nonnull Long codContent) {
        Optional<T> contentEntity = find(codContent);
        if(contentEntity.isPresent()) {
            delete(contentEntity.get());
        }
    }

    protected T createContent(InputStream in, long length, String hashSha1) {
        DeflaterInputStream inZip      = new DeflaterInputStream(in, new Deflater(Deflater.BEST_COMPRESSION));
        T                   fileEntity = createInstance();
        fileEntity.setContent(getSession().getLobHelper().createBlob(inZip, length));
        fileEntity.setHashSha1(hashSha1);
        fileEntity.setSize(length);
        fileEntity.setInclusionDate(new Date());
        return fileEntity;
    }

    protected T createInstance() {
        try {
            return tipo.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw SingularException.rethrow(e);
        }
    }
}