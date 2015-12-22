package br.net.mirante.singular.form.mform;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.net.mirante.singular.form.mform.MTipoComposto.FieldMapOfRecordType;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;

public class MIComposto
        extends MInstancia
        implements ICompositeInstance, MSelectionableInstance {

    private FieldMapOfRecordInstance fields;

    @Override
    public MTipoComposto<?> getMTipo() {
        return (MTipoComposto<?>) super.getMTipo();
    }

    @Override
    public Object getValor() {
        return getCampos();
    }

    @Override
    public boolean isEmptyOfData() {
        return fields == null || fields.stream().allMatch(i -> i.isEmptyOfData());
    }

    /**
     * Retorna apenas os campos do tipo que já foram instanciados.
     *
     * @return instancias dos campos
     */
    public Collection<MInstancia> getCampos() {
        return (fields == null) ? Collections.emptyList() : fields.getFields();
    }

    /**
     * Retorna todos os campos do tipo, instanciando os que ainda não foram.
     *
     * @return instancias dos campos
     */
    public Collection<MInstancia> getAllFields() {
        for (MTipo<?> field : getMTipo().getFields())
            getCampo(field.getNomeSimples());
        return getCampos();
    }

    @Override
    public Collection<MInstancia> getChildren() {
        return getCampos();
    }
    @Override
    public Collection<MInstancia> getAllChildren() {
        return getAllFields();
    }

    @Override
    public Stream<? extends MInstancia> stream() {
        return fields == null ? Stream.empty() : fields.stream();
    }

    @Override
    public MInstancia getCampo(String path) {
        return getCampo(new LeitorPath(path));
    }

    @Override
    final MInstancia getCampoLocal(LeitorPath leitor) {
        int fieldIndex = findIndexTrecho(leitor);
        MInstancia instancia = (fields == null) ? null : fields.getByIndex(fieldIndex);
        if (instancia == null) {
            instancia = createField(fieldIndex);
        }
        return instancia;
    }

    @Override
    final MInstancia getCampoLocalSemCriar(LeitorPath leitor) {
        int fieldIndex = findIndexTrecho(leitor);
        return (fields == null) ? null : fields.getByIndex(fieldIndex);
    }

    public <T extends MInstancia> T getFilho(MTipo<T> tipoPai) {
        throw new RuntimeException("Método não implementado");
    }

    @Override
    public final void setValor(String pathCampo, Object valor) {
        setValor(new LeitorPath(pathCampo), valor);
    }

    private FieldMapOfRecordType getFieldsDef() {
        return getMTipo().getFieldsConsolidated();
    }

    @Override
    void setValor(LeitorPath leitorPath, Object valor) {
        int fieldIndex = findIndexTrecho(leitorPath);
        MInstancia instancia = (fields == null) ? null : fields.getByIndex(fieldIndex);
        if (instancia == null) {
            if (valor == null) {
                return;
            }
            instancia = createField(fieldIndex);
        }
        if (leitorPath.isUltimo()) {
            if (valor == null) {
                MInstancia child = fields.getByIndex(fieldIndex);
                if (child != null) {
                    child.internalOnRemove();
                    fields.remove(fieldIndex);
                }
            } else {
                instancia.setValor(valor);
            }
        } else {
            instancia.setValor(leitorPath.proximo(), valor);
        }
    }

    private MInstancia createField(int fieldIndex) {
        MInstancia instancia;
        MTipo<?> tipoCampo = getFieldsDef().getByIndex(fieldIndex);
        instancia = tipoCampo.newInstance(getDocument());
        instancia.setPai(this);
        if (fields == null) {
            fields = new FieldMapOfRecordInstance(getFieldsDef());
        }
        fields.set(fieldIndex, instancia);
        return instancia;
    }

    private int findIndexTrecho(LeitorPath leitor) {
        if (leitor.isIndice()) {
            throw new SingularFormException(leitor.getTextoErro(this, "Não é uma lista"));
        }
        int fieldIndex = getFieldsDef().findIndex(leitor.getTrecho());
        if (fieldIndex == -1) {
            throw new SingularFormException(leitor.getTextoErro(this, "Não é um campo definido"));
        }
        return fieldIndex;
    }

    @Override
    public final <T extends Object> T getValor(String pathCampo, Class<T> classeDestino) {
        return getValor(new LeitorPath(pathCampo), classeDestino);
    }

    @Override
    final <T extends Object> T getValorWithDefaultIfNull(LeitorPath leitor, Class<T> classeDestino) {
        if (fields != null) {
            MInstancia instancia = fields.getByIndex(findIndexTrecho(leitor));
            if (instancia != null) {
                return instancia.getValorWithDefaultIfNull(leitor.proximo(), classeDestino);
            }
        }
        MTipo<?> tipo = MFormUtil.resolverTipoCampo(getMTipo(), leitor);
        return tipo.getValorAtributoOrDefaultValueIfNull(classeDestino);
    }

    private final static class FieldMapOfRecordInstance {

        private final MInstancia[] instances;

        public FieldMapOfRecordInstance(FieldMapOfRecordType fieldsDef) {
            this.instances = new MInstancia[fieldsDef.size()];
        }

        public MInstancia getByIndex(int fieldIndex) {
            return instances[fieldIndex];
        }

        public void remove(int fieldIndex) {
            instances[fieldIndex] = null;
        }

        public void set(int fieldIndex, MInstancia instance) {
            instances[fieldIndex] = instance;
        }

        public List<MInstancia> getFields() {
            return stream().collect(Collectors.toList());
        }

        public Stream<MInstancia> stream() {
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
        MIComposto other = (MIComposto) obj;
        if (!getMTipo().equals(other.getMTipo())) {
            return false;
        }
        if(getFieldId() != null){
            return getFieldId().equals(other.getFieldId());
        }
        return Objects.equals(fields, other.fields);
    }

    @Override
    public void setFieldId(Object value) {
        if(getMTipo().id_sel == null ){
            getMTipo().configureKeyValueFields();
        }
        setValor(key(), value);
    }

    public void setFieldValue(Object value) {
        if(getMTipo().val_sel == null ){
            getMTipo().configureKeyValueFields();
        }
        setValor(value(), value);
    }

    private String value() {
//        return getAndCheckKeyValueAttributes(getMTipo().VALUE_FIELD);
        if(getMTipo().val_sel == null ){
            getMTipo().configureKeyValueFields();
        }
//        return (String) getCampo(getMTipo().val_sel).getValor();
        return getMTipo().val_sel;
    }

    public String getFieldId() {
        return getValorString(key());
    }

    public String getFieldValue() {
        return getValorString(value());
    }

    @Override
    public void setValue(Object key, Object value) {
        setFieldId(key);
        setFieldValue(value);
    }

    private String key() {
//        return getAndCheckKeyValueAttributes(getMTipo().ID_FIELD);
        if(getMTipo().id_sel == null ){
            getMTipo().configureKeyValueFields();
        }
//        return (String) getCampo(getMTipo().id_sel).getValor();
        return getMTipo().id_sel;
    }


    private String getAndCheckKeyValueAttributes(AtrRef<MTipoString, MIString, String> attr) {
        String value = getValorAtributo(attr);
        if(value == null){
            ((MTipoComposto)getMTipo()).configureKeyValueFields();
            value = getValorAtributo(attr);
        }
        return value;
    }

    @Override
    public void setValor(Object value) {
        if(value instanceof MIComposto) { setValue((MIComposto) value);
        }else if(value instanceof List){    setValue((List) value);
        }else{                              super.setValor(value);
        }
    }

    private void setValue(List<MInstancia> values) {
        this.setValue(valueOf(values.get(0)), valueOf(values.get(1)));
    }

    private void setValue(MIComposto item) {
        this.setValue(item.getFieldId(),item.getFieldValue());
    }

    private Object valueOf(MInstancia instance) {
        return instance.getValor();
    }

}
