/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.net.mirante.singular.form.mform.STypeComposite.FieldMapOfRecordType;
import br.net.mirante.singular.form.mform.util.transformer.Value;

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
    //TODO: Won't "isEmpty" is enough? the "ofData" seems kind of redundant.
    public boolean isEmptyOfData() {
        return fields == null || fields.stream().allMatch(i -> i.isEmptyOfData());
    }

    /**
     * Retorna apenas os campos do tipo que já foram instanciados.
     *
     * @return instancias dos campos
     */
    public Collection<SInstance> getFields() {
        return (fields == null) ? Collections.emptyList() : fields.getFields();
    }

    /**
     * Retorna todos os campos do tipo, instanciando os que ainda não foram.
     *
     * @return instancias dos campos
     */
    public Collection<SInstance> getAllFields() {
        for (SType<?> field : getType().getFields())
            getField(field.getNameSimple());
        return getFields();
    }

    @Override
    public Collection<SInstance> getChildren() {
        return getFields();
    }

    @Override
    public Collection<SInstance> getAllChildren() {
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

    @Override
    final SInstance getFieldLocal(PathReader pathReader) {
        return getFieldByIndexOrCreate(findFieldIndex(pathReader));
    }

    @Override
    Optional<SInstance> getFieldLocalOpt(PathReader pathReader) {
        int fieldIndex = findFieldIndexOpt(pathReader);
        if (fieldIndex != -1) {
            return Optional.of(getFieldByIndexOrCreate(fieldIndex));
        }
        return Optional.empty();
    }

    @Override
    final SInstance getFieldLocalWithoutCreating(PathReader pathReader) {
        int fieldIndex = findFieldIndex(pathReader);
        return (fields == null) ? null : fields.getByIndex(fieldIndex);
    }

    public <T extends SInstance> T getChildren(SType<T> tipoPai) {
        throw new RuntimeException("Método não implementado");
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
     * @param field
     *            Tipo do campo filho
     * @return
     */
    public Object getValue(SType<?> field) {
        return getValue(field.getNameSimple());
    }

    private FieldMapOfRecordType getFieldsDef() {
        return getType().getFieldsConsolidated();
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

    private SInstance getFieldByIndexOrCreate(int fieldIndex) {
        SInstance instancia = (fields == null) ? null : fields.getByIndex(fieldIndex);
        if (instancia == null) {
            instancia = createField(fieldIndex);
        }
        return instancia;
    }

    private SInstance createField(int fieldIndex) {
        SInstance instancia;
        SType<?> tipoCampo = getFieldsDef().getByIndex(fieldIndex);
        instancia = tipoCampo.newInstance(getDocument());
        instancia.setParent(this);
        if (fields == null) {
            fields = new FieldMapOfRecordInstance(getFieldsDef());
        }
        fields.set(fieldIndex, instancia);
        return instancia;
    }

    /**
     * Procura o índice do elemento solicitado dentro da lista de campo ou
     * retorna -1 se o campo não existir no tipo composto.
     */
    private int findFieldIndexOpt(PathReader pathReader) {
        if (pathReader.isIndex()) {
            throw new SingularFormException(pathReader.getErroMsg(this, "Não é uma lista"));
        }
        return getFieldsDef().findIndex(pathReader.getTrecho());
    }

    /**
     * Procura o índice do elemento solicitado dentro da lista de campo ou
     * dispara exception se o campo não existir no tipo composto.
     */
    private int findFieldIndex(PathReader pathReader) {
        int fieldIndex = findFieldIndexOpt(pathReader);
        if (fieldIndex == -1) {
            throw new SingularFormException(pathReader.getErroMsg(this, "Não é um campo definido"));
        }
        return fieldIndex;
    }

    @Override
    public final <T extends Object> T getValue(String fieldPath, Class<T> resultClass) {
        return getValue(new PathReader(fieldPath), resultClass);
    }

    @Override
    final <T extends Object> T getValueWithDefaultIfNull(PathReader pathReader, Class<T> resultClass) {
        if (fields != null) {
            SInstance instancia = fields.getByIndex(findFieldIndex(pathReader));
            if (instancia != null) {
                return instancia.getValueWithDefaultIfNull(pathReader.next(), resultClass);
            }
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
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SIComposite other = (SIComposite) obj;
        if (!getType().equals(other.getType())) {
            return false;
        }
        return Objects.equals(fields, other.fields);
    }

    private final static class FieldMapOfRecordInstance {

        private final SInstance[] instances;

        public FieldMapOfRecordInstance(FieldMapOfRecordType fieldsDef) {
            this.instances = new SInstance[fieldsDef.size()];
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
            if (!Arrays.equals(instances, other.instances))
                return false;
            return true;
        }
    }
}
