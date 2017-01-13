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

package org.opensingular.form;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.opensingular.form.internal.PathReader;
import org.opensingular.form.util.transformer.Value;

public class SIComposite extends SInstance implements ICompositeInstance {

    private FieldMapOfRecordInstance fields;

    @Override
    public STypeComposite<?> getType() {
        return (STypeComposite<?>) super.getType();
    }

    @Override
    public Object getValue() {
        return getFields();
    }

    @Override
    public void clearInstance() {
        getFields().forEach(SInstance::clearInstance);
    }

    @Override
    public boolean isEmptyOfData() {
        return fields == null || fields.stream().allMatch(SInstance::isEmptyOfData);
    }

    /**
     * Retorna apenas os campos do tipo que já foram instanciados.
     *
     * @return instancias dos campos
     */
    public List<SInstance> getFields() {
        return (fields == null) ? Collections.emptyList() : fields.getFields();
    }

    /**
     * Retorna todos os campos do tipo, instanciando os que ainda não foram.
     *
     * @return instancias dos campos
     */
    public List<SInstance> getAllFields() {
        for (SType<?> field : getType().getFields())
            getField(field.getNameSimple());
        return getFields();
    }

    @Override
    public List<SInstance> getChildren() {
        return getFields();
    }

    @Override
    public List<SInstance> getAllChildren() {
        return getAllFields();
    }

    @Override
    public Stream<? extends SInstance> stream() {
        return fields == null ? Stream.empty() : fields.stream();
    }

    @Override
    public SInstance getField(String path) {
        return getField(new PathReader(path));
    }

    @Override
    public Optional<SInstance> getFieldOpt(String path) {
        return getFieldOpt(new PathReader(path));
    }

    /**
     * Retorna o campo cujo o nome seja igual ao do tipo informado e verifica se o campo encontrado é do mesmo tipo
     * informado. Caso não seja do mesmo tipo, dispara uma exception.
     */
    public <II extends SInstance> II getField(SType<II> type) {
        SInstance instance = getField(type.getNameSimple());
        type.checkIfIsInstanceOf(instance);
        return (II) instance;
    }

    @Override
    final SInstance getFieldLocal(PathReader pathReader) {
        return getField(findFieldIndex(pathReader));
    }

    @Override
    Optional<SInstance> getFieldLocalOpt(PathReader pathReader) {
        int fieldIndex = findFieldIndexOpt(pathReader);
        if (fieldIndex != -1) {
            return Optional.of(getField(fieldIndex));
        }
        return Optional.empty();
    }

    @Override
    final SInstance getFieldLocalWithoutCreating(PathReader pathReader) {
        if (fields != null) {
            return fields.getByIndex(findFieldIndex(pathReader));
        }
        return null;
    }

    @Override
    public void setValue(Object obj) {
        if(obj instanceof SIComposite){
            clearInstance();
            Value.hydrate(this, Value.dehydrate((SInstance) obj));
        }else{
            throw new SingularFormException("SIComposite só suporta valores de mesmo tipo");
        }
    }

    @Override
    public final void setValue(String pathCampo, Object valor) {
        setValue(new PathReader(pathCampo), valor);
    }

    /**
     * Configura o valor de um tipo filho imediato do tipo composto ao qual essa
     * instancia se refere.
     * <p>
     * O Mtipo informado já precisa estar previamente configurado nesse
     * MtipoComposto
     *
     * @param campo
     *            Referencia ao mtipo filho do composto
     * @param valor
     *            Valor para o mtipo referenciado.
     */
    public final void setValue(SType<?> campo, Object valor) {
        setValue(new PathReader(campo.getNameSimple()), valor);
    }

    /**
     * Obtém o valor de um campo a partir do seu tipo O campo deve ser filho
     * imediato desse MTipo
     *
     * @param field Tipo do campo filho
     */
    public Object getValue(SType<?> field) {
        return getValue(field.getNameSimple());
    }

    @Override
    void setValue(PathReader pathReader, Object value) {
        int fieldIndex = findFieldIndex(pathReader);
        SInstance instancia = (fields == null) ? null : fields.getByIndex(fieldIndex);
        if (instancia == null) {
            if (value == null) {
                return;
            }
            instancia = createField(fieldIndex);
        }
        if (pathReader.isLast()) {
            if (value == null) {
                SInstance child = fields.getByIndex(fieldIndex);
                if (child != null) {
                    child.internalOnRemove();
                    fields.remove(fieldIndex);
                }
            } else {
                instancia.setValue(value);
            }
        } else {
            instancia.setValue(pathReader.next(), value);
        }
    }

    /**
     * Retorna o campo da posição indicada. Se o campo ainda não existir, cria-o.
     */
    public SInstance getField(int fieldIndex) {
        SInstance instance = (fields == null) ? null : fields.getByIndex(fieldIndex);
        return instance != null ? instance : createField(fieldIndex);
    }

    private SInstance createField(int fieldIndex) {
        SType<?> fieldType = getType().getField(fieldIndex);
        SInstance instance = fieldType.newInstance(getDocument());
        if (fields == null) {
            fields = new FieldMapOfRecordInstance(getType().size());
        }
        fields.set(fieldIndex, instance);
        instance.setParent(this);
        return instance;
    }

    /**
     * Procura o índice do elemento solicitado dentro da lista de campo ou
     * retorna -1 se o campo não existir no tipo composto.
     */
    private int findFieldIndexOpt(PathReader pathReader) {
        if (pathReader.isIndex()) {
            throw new SingularFormException(pathReader.getErrorMsg(this, "Não é uma lista"));
        }
        return getType().findIndexOf(pathReader.getToken());
    }

    /**
     * Procura o índice do elemento solicitado dentro da lista de campo ou
     * dispara exception se o campo não existir no tipo composto.
     */
    private int findFieldIndex(PathReader pathReader) {
        int fieldIndex = findFieldIndexOpt(pathReader);
        if (fieldIndex == -1) {
            throw new SingularFormException(pathReader.getErrorMsg(this, "Não é um campo definido"));
        }
        return fieldIndex;
    }

    @Override
    public final <T> T getValue(String fieldPath, Class<T> resultClass) {
        return getValue(new PathReader(fieldPath), resultClass);
    }

    @Override
    final <T> T getValueWithDefaultIfNull(PathReader pathReader, Class<T> resultClass) {
        SInstance instance = getFieldLocalWithoutCreating(pathReader);
        if (instance != null) {
            return instance.getValueWithDefaultIfNull(pathReader.next(), resultClass);
        }
        SType<?> tipo = SFormUtil.resolveFieldType(getType(), pathReader);
        return tipo.getAttributeValueOrDefaultValueIfNull(resultClass);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fields == null) ? 0 : fields.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SIComposite other = (SIComposite) obj;
        return getType().equals(other.getType()) && Objects.equals(fields, other.fields);
    }

    private final static class FieldMapOfRecordInstance {

        private final SInstance[] instances;

        public FieldMapOfRecordInstance(int size) {
            this.instances = new SInstance[size];
        }

        public SInstance getByIndex(int fieldIndex) {
            return instances[fieldIndex];
        }

        public void remove(int fieldIndex) {
            instances[fieldIndex] = null;
        }

        public void set(int fieldIndex, SInstance instance) {
            instances[fieldIndex] = instance;
        }

        public List<SInstance> getFields() {
            return stream().collect(Collectors.toList());
        }

        public Stream<SInstance> stream() {
            return Arrays.stream(instances).filter(i -> i != null);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode(instances);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            FieldMapOfRecordInstance other = (FieldMapOfRecordInstance) obj;
            return Arrays.equals(instances, other.instances);
        }
    }
}
