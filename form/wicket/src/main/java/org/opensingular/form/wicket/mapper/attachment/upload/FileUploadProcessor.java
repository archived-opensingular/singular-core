/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.wicket.mapper.attachment.upload;

import org.apache.commons.fileupload.FileItem;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.wicket.mapper.attachment.upload.info.FileUploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadInfo;
import org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo;
import org.opensingular.lib.commons.base.SingularException;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo
        .ARQUIVO_NAO_PODE_SER_DE_TAMANHO_0_ZERO;
import static org.opensingular.form.wicket.mapper.attachment.upload.info.UploadResponseInfo
        .TIPO_DE_ARQUIVO_NAO_PERMITIDO;

public class FileUploadProcessor implements Serializable {


    public List<UploadResponseInfo> process(FileItem item, UploadInfo upInfo, FileUploadManager upManager) throws SingularException {

        final List<UploadResponseInfo> responses = new ArrayList<>();

        if (!item.isFormField()) {

            // Garante que virar apenas o nome do arquivo sem path
            final String originalFilename = checkValidName(item);
            final String contentType      = lowerCase(item.getContentType());
            final String extension        = lowerCase(substringAfterLast(originalFilename, "."));

            if (item.getSize() == 0) {
                responses.add(new UploadResponseInfo(originalFilename, ARQUIVO_NAO_PODE_SER_DE_TAMANHO_0_ZERO));

            } else if (!(upInfo.isFileTypeAllowed(contentType) || upInfo.isFileTypeAllowed(extension))) {
                responses.add(new UploadResponseInfo(originalFilename, TIPO_DE_ARQUIVO_NAO_PERMITIDO));

            } else {
                try (InputStream in = item.getInputStream()) {
                    final FileUploadInfo fileInfo = upManager.createFile(upInfo, originalFilename, in);
                    responses.add(new UploadResponseInfo(fileInfo.getAttachmentRef()));
                } catch (Exception e){
                    throw SingularException.rethrow(e.getMessage(), e);
                }
            }
        }

        return responses;
    }

    private String checkValidName(FileItem item) {
        String n = item.getName();
        if (n != null) {
            if (n.indexOf('\'') != -1 || n.indexOf('/') != -1) {
                throw new SingularFormException("Invalid file name: " + n);
            }
        }
        return n;
    }

}
