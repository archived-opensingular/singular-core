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

package org.opensingular.form.io;

import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.SInstance;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.document.ServiceRegistry;
import org.opensingular.form.internal.xml.MElement;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>
 * Classe de suporte a serialização e deserialização de
 * {@link SDocument} e
 * {@link SInstance}.
 * </p>
 * <p>
 * Tendo em vista que as definições de tipos não são serializadas (vão apenas os
 * dados das instâncias), ao deserealizar é necessário recuperar a definição do
 * tipo no correto dicionário.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public class FormSerializationUtil {

    private FormSerializationUtil() {}

    /**
     * <p>
     * Gera uma vesão serializável da instancia. Implica em serializar todo o
     * documento associado a instância, contudo guarda o path da instancia alvo
     * para poder recuperar corretamente depois.
     * </p>
     * <p>
     * Não serializa a definição do tipo (dicionário). Guarda apenas o nome do
     * tipo.
     * </p>
     */
    public static FormSerialized toSerializedObject(SInstance instance) {
        FormSerialized fs = toSerialized(instance.getDocument());
        defineFocusField(instance, fs);
        return fs;
    }

    private static void defineFocusField(SInstance instance, FormSerialized fs) {
        if (instance.getDocument().getRoot() != instance) {
            fs.setFocusFieldPath(instance.getPathFromRoot());
        }
    }

    /**
     * <p>
     * Gera uma vesão serializável do documento.
     * </p>
     * <p>
     * Não serializa a definição do tipo (dicionário). Guarda apenas o nome do
     * tipo.
     * </p>
     */
    private static FormSerialized toSerialized(SDocument document) {
        SInstance root = document.getRoot();
        MElement xml = MformPersistenciaXML.toXMLPreservingRuntimeEdition(root);
        MElement annotations = null;
        if (!root.asAtrAnnotation().allAnnotations().isEmpty()) {
            annotations = MformPersistenciaXML.toXMLPreservingRuntimeEdition(root.asAtrAnnotation().persistentAnnotations());
        }

        checkIfSerializable(root);
        FormSerialized fs = new FormSerialized(document.getRootRefType().get(), root.getType().getName(), xml, annotations,
            document.getDocumentFactoryRef());
        serializeServices(document, fs);
        fs.setValidationErrors(document.getValidationErrors());
        return fs;
    }

    /**
     * Verifica se a instância atende os critérios necessários para ser
     * serializável. Para tanto é necessário que tenha sido criado a partir de
     * um {@link SDocumentFactory} e com o uso {@link RefType}.
     *
     * @throws SingularFormException
     *             Se não atender os critérios
     */
    public final static void checkIfSerializable(SInstance instance) {
        SDocument document = instance.getDocument();
        if (!document.getRootRefType().isPresent()) {
            throw new SingularFormException("Não foi configurado o rootRefType no Document da instância, o que impedirá a "
                + "serialização/deserialização do mesmo. " + "A instância deve ser criada usando " + SDocumentFactory.class.getName(),
                instance);
        }
        if (document.getDocumentFactoryRef() == null) {
            throw new SingularFormException("Não foi configurado o DocumentFactory no Document da instância, o que impedirá a "
                + "serialização/deserialização do mesmo. " + "A instância deve ser criada usando " + SDocumentFactory.class.getName(),
                instance);
        }
        if (document.annotations() != null) {
            if (!document.annotations().getDocument().getRootRefType().isPresent()) {
                throw new SingularFormException("Não foi configurado o rootRefType nas anotações da instância, o que impedirá a "
                    + "serialização/deserialização do mesmo. ", instance);
            }
            if (document.annotations().getDocument().getDocumentFactoryRef() == null) {
                throw new SingularFormException("Não foi configurado o DocumentFactory nas anotações da instância, o que impedirá a "
                    + "serialização/deserialização do mesmo. ", instance);
            }
        }
    }

    private static void serializeServices(SDocument document, FormSerialized fs) {
        Map<String, ServiceRegistry.Pair> services = document.getLocalServices();
        if (!services.isEmpty()) {
            if (!(services instanceof Serializable)) {
                throw new SingularFormException("The Document service map is not Serializable.");
            }
            fs.setServices(services);
        }
    }

    /**
     * Recupera a instância e o documento que foi serializado. Se foi
     * originalmente serializado um documento, então retorna a instância raiz do
     * documento. Se foi serialziado um sub parte do documento, retorna a
     * instancia da sub parte, mas na prática deserializa todo o documento.
     *
     * @param fs
     *            Dado a ser deserializado
     * @return Sempre diferente de Null
     * @exception SingularFormException
     *                Senão encontrar o dicionário ou tipo necessário.
     */
    public static SInstance toInstance(FormSerialized fs) {
        try {
            SInstance root = MformPersistenciaXML.fromXML(fs.getRefRootType(), fs.getXml(), fs.getSDocumentFactoryRef().get());
            deserializeServices(fs.getServices(), root.getDocument());
            MformPersistenciaXML.annotationLoadFromXml(root, fs.getAnnotations());
            root.getDocument().setValidationErrors(fs.getValidationErrors());
            return defineRoot(fs, root);
        } catch (Exception e) {
            throw deserializingError(fs, e);
        }
    }

    private static void deserializeServices(Map<String, ServiceRegistry.Pair> services, SDocument document) {
        if (services != null) {
            services.entrySet().stream().forEach(entry -> bindService(document, entry));
        }
    }

    private static void bindService(SDocument document, Map.Entry<String, ServiceRegistry.Pair> entry) {
        ServiceRegistry.Pair p = entry.getValue();
        document.bindLocalService(entry.getKey(), (Class<Object>) p.type, p.provider);
    }

    private static SInstance defineRoot(FormSerialized fs, SInstance root) {
        if (StringUtils.isBlank(fs.getFocusFieldPath())) {
            return root;
        }
        return ((ICompositeInstance) root).getField(fs.getFocusFieldPath());
    }

    private static SingularFormException deserializingError(FormSerialized fs, Exception e) {
        String msg = "Error when deserializing " + fs.getRootTypeName();
        if (!StringUtils.isBlank(fs.getFocusFieldPath())) {
            msg += " with subPath '" + fs.getFocusFieldPath() + '\'';
        }
        return new SingularFormException(msg, e);
    }
}
