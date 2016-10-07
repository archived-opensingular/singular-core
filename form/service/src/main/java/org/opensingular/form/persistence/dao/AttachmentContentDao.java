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

import java.io.InputStream;
import java.util.Date;

import javax.transaction.Transactional;

import org.opensingular.form.persistence.entity.AttachmentContentEntitty;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.form.io.HashAndCompressInputStream;
import org.opensingular.lib.support.persistence.BaseDAO;

@SuppressWarnings({"serial","unchecked"})
@Transactional(Transactional.TxType.MANDATORY)
public class AttachmentContentDao<T extends AttachmentContentEntitty> extends BaseDAO<T, Long> {

    public AttachmentContentDao() {
        super((Class<T>) AttachmentContentEntitty.class);
    }
    
    protected AttachmentContentDao(Class<T> tipo) {
        super(tipo);
    }
    
    public T insert(T o) {
        getSession().save(o);
        return o;
    }

    public T insert(InputStream is, long length) {
        return insert(createContent(is, length));
    }
    
    public void delete(Long codContent) {
        T contentEntitty = find(codContent);
        delete(contentEntitty);
    }

    protected T createContent(InputStream in, long length) {
        HashAndCompressInputStream inHash = new HashAndCompressInputStream(in);
        T fileEntity = createInstance();
        fileEntity.setContent(getSession().getLobHelper().createBlob(inHash, length));
        fileEntity.setHashSha1(inHash.getHashSHA1());
        fileEntity.setSize(length);
        fileEntity.setInclusionDate(new Date());
        return fileEntity;
    }
    
    protected T createInstance() {
        try {
            return tipo.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SingularException(e);
        }
    }
}