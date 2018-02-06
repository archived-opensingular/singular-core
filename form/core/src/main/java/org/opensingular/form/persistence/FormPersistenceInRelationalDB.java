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

package org.opensingular.form.persistence;

import static org.opensingular.form.persistence.Criteria.and;
import static org.opensingular.form.persistence.Criteria.emptyCriteria;
import static org.opensingular.form.persistence.Criteria.isEqualTo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.relational.RelationalColumn;
import org.opensingular.form.persistence.relational.RelationalData;
import org.opensingular.form.persistence.relational.RelationalFK;
import org.opensingular.form.persistence.relational.RelationalSQL;
import org.opensingular.form.persistence.relational.RelationalSQLCommmand;

/**
 * Form persistence based on relational database managers.
 *
 * @author Edmundo Andrade
 */
public class FormPersistenceInRelationalDB<TYPE extends STypeComposite<INSTANCE>, INSTANCE extends SIComposite>
        implements FormRespository<TYPE, INSTANCE> {
    protected RelationalDatabase db;
    private final SDocumentFactory documentFactory;
    private final Class<TYPE> type;
    private FormKeyManager<FormKeyRelational> formKeyManager;

    public FormPersistenceInRelationalDB(RelationalDatabase db, SDocumentFactory documentFactory, Class<TYPE> type) {
        this.db = db;
        this.documentFactory = documentFactory;
        this.type = type;
    }

    @Nonnull
    public FormKey keyFromObject(@Nonnull Object objectValueToBeConverted) {
        return getFormKeyManager().keyFromObject(objectValueToBeConverted);
    }

    @Nonnull
    public FormKey insert(@Nonnull INSTANCE instance, Integer inclusionActor) {
        return insertInternal(instance, inclusionActor);
    }

    public void delete(@Nonnull FormKey key) {
        INSTANCE mainInstance = load(key);
        mainInstance.getAllChildren().stream().filter(field -> RelationalSQL.isListWithTableBound(field.getType()))
                .forEach(field -> {
                    SIList<SIComposite> listInstance = mainInstance.getFieldList(field.getType().getNameSimple(),
                            SIComposite.class);
                    for (SIComposite item : listInstance.getChildren()) {
                        String manyToManyTable = manyToManyTable(item);
                        if (manyToManyTable != null) {
                            executeManyToManyDelete(mainInstance, item, manyToManyTable);
                        }
                        deleteInternal(item.getType(), FormKey.fromInstance(item));
                    }
                });
        deleteInternal(createType(), key);
    }

    private void executeManyToManyDelete(INSTANCE sourceInstance, SIComposite targetInstance, String manyToManyTable) {
        FormKeyRelational sourceKey = (FormKeyRelational) FormKey.fromInstance(sourceInstance);
        FormKeyRelational targetKey = (FormKeyRelational) FormKey.fromInstance(targetInstance);
        StringBuilder sqlBuilder = new StringBuilder("DELETE FROM ");
        sqlBuilder.append(manyToManyTable);
        sqlBuilder.append(" WHERE ");
        List<Object> params = new ArrayList<>();
        String delim = "";
        for (String pkColumn : RelationalSQL.tablePK(sourceInstance.getType())) {
            params.add(sourceKey.getColumnValue(pkColumn));
            sqlBuilder.append(delim);
            sqlBuilder.append(targetInstance.getParent().asSQL().getManyToManySourceKeyColumns());
            sqlBuilder.append(" = ?");
            delim = " AND ";
        }
        for (String pkColumn : RelationalSQL.tablePK(targetInstance.getType())) {
            params.add(targetKey.getColumnValue(pkColumn));
            sqlBuilder.append(delim);
            sqlBuilder.append(targetInstance.getParent().asSQL().getManyToManyTargetKeyColumns());
            sqlBuilder.append(" = ?");
            delim = " AND ";
        }
        db.exec(sqlBuilder.toString(), params);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private TYPE createType() {
        return (TYPE) RefType.of(type).get();
    }

    public void update(@Nonnull INSTANCE instance, Integer inclusionActor) {
        updateInternal(instance, load(FormKey.fromInstance(instance)), inclusionActor);
    }

    @Nonnull
    public FormKey insertOrUpdate(@Nonnull INSTANCE instance, Integer inclusionActor) {
        if (isPersistent(instance)) {
            update(instance, inclusionActor);
            return FormKey.fromInstance(instance);
        }
        return insert(instance, inclusionActor);
    }

    public boolean isPersistent(@Nonnull INSTANCE instance) {
        return FormKey.containsKey(instance);
    }

    @Nonnull
    public FormKey newVersion(@Nonnull INSTANCE instance, Integer inclusionActor, boolean keepAnnotations) {
        throw new SingularFormException("Method not implemented.");
    }

    @Nonnull
    public INSTANCE load(@Nonnull FormKey key) {
        return loadOpt(key).orElseThrow(() -> new SingularFormNotFoundException(key));
    }

    @Nonnull
    public Optional<INSTANCE> loadOpt(@Nonnull FormKey key) {
        return Optional.ofNullable(loadInternal(key));
    }

    @Nonnull
    public List<INSTANCE> loadAll(long first, long max) {
        return loadAllInternal(first, max, emptyCriteria());
    }

    @Nonnull
    public List<INSTANCE> loadAll() {
        return loadAllInternal(null, null, emptyCriteria());
    }

    @Nonnull
    public List<INSTANCE> list(Criteria criteria, OrderByField... orderBy) {
        return loadAllInternal(null, null, criteria, orderBy);
    }

    @Nonnull
    public List<INSTANCE> list(SIComposite example, OrderByField... orderBy) {
        List<Criteria> operands = new ArrayList<>();
        RelationalSQL.getFields(example).forEach(field -> {
            Object value = fieldValue(example, field);
            if (value != null) {
                operands.add(isEqualTo(field, value));
            }
        });
        return list(and(operands.toArray(new Criteria[operands.size()])), orderBy);
    }

    private Object fieldValue(SIComposite instance, SType<?> field) {
        String fieldPath = field.getName().replaceFirst(instance.getType().getName() + ".", "");
        return RelationalSQL.fieldValue(instance.getField(fieldPath));
    }

    public long countAll() {
        long result = 0;
        RelationalSQL query = RelationalSQL.selectCount(createType());
        for (RelationalSQLCommmand command : query.toSQLScript()) {
            result += (long) db.query(command.getSQL(), command.getParameters()).get(0)[0];
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public INSTANCE createInstance() {
        return (INSTANCE) documentFactory.createInstance(RefType.of(type));
    }

    @Nonnull
    public FormKeyManager<FormKeyRelational> getFormKeyManager() {
        if (formKeyManager == null) {
            formKeyManager = new FormKeyManager<>(FormKeyRelational.class, e -> addInfo(e));
        }
        return formKeyManager;
    }

    @Nonnull
    protected SingularFormPersistenceException addInfo(@Nonnull SingularFormPersistenceException exception) {
        return exception.add("persistence", toString());
    }

    @Nullable
    protected INSTANCE loadInternal(@Nonnull FormKey key) {
        INSTANCE mainInstance = null;
        TYPE mainType = createType();
        RelationalSQL query = RelationalSQL.select(mainType.getContainedTypes()).where(mainType, key);
        for (RelationalSQLCommmand command : query.toSQLScript()) {
            for (INSTANCE instance : executeSelectCommand(command)) {
                mainInstance = instance;
                break;
            }
        }
        for (SType<?> field : mainType.getContainedTypes()) {
            if (RelationalSQL.isListWithTableBound(field)) {
                executeSelectField(key, mainInstance, mainType, field);
            }
        }
        return mainInstance;
    }

    protected void executeSelectField(@Nonnull FormKey key, INSTANCE mainInstance, TYPE mainType, SType<?> field) {
        SIList<SIComposite> listInstance = mainInstance.getFieldList(field.getNameSimple(), SIComposite.class);
        for (SType<?> detail : field.getLocalTypes()) {
            STypeComposite<?> detailType = (STypeComposite<?>) detail.getSuperType();
            for (RelationalSQLCommmand command : RelationalSQL.select(detailType.getContainedTypes())
                    .where(mainType, key).toSQLScript()) {
                executeSelectCommandIntoSIList(command, listInstance);
            }
        }
    }

    @Nonnull
    protected List<INSTANCE> loadAllInternal(Long first, Long max, Criteria criteria, OrderByField... orderBy) {
        List<INSTANCE> result = new ArrayList<>();
        RelationalSQL query = RelationalSQL.select(createType().getContainedTypes()).where(criteria).limit(first, max)
                .orderBy(orderBy);
        for (RelationalSQLCommmand command : query.toSQLScript()) {
            result.addAll(executeSelectCommand(command));
        }
        return result;
    }

    protected FormKey insertInternal(@Nonnull SIComposite instance, Integer inclusionActor) {
        List<RelationalData> toList = new ArrayList<>();
        RelationalSQL.persistenceStrategy(instance.getType()).save(instance, toList);
        Set<SIComposite> targets = new LinkedHashSet<>();
        toList.forEach(data -> targets.add((SIComposite) data.getTupleKeyRef()));
        reorderTargets(targets);
        for (SIComposite target : targets) {
            for (RelationalSQLCommmand command : RelationalSQL.insert(target).toSQLScript()) {
                executeInsertCommand(command);
                String manyToManyTable = manyToManyTable(command);
                if (manyToManyTable != null) {
                    executeManyToManyInsert(instance, command.getInstance(), manyToManyTable);
                }
            }
        }
        return FormKey.fromInstance(instance);
    }

    private void reorderTargets(Set<SIComposite> targets) {
        List<SType<?>> tableTargets = new ArrayList<>();
        targets.forEach(target -> tableTargets.add(RelationalSQL.tableContext(target.getType())));
        Map<String, RelationalFK> joinMap = RelationalSQL.createJoinMap(tableTargets);
        List<SIComposite> instances = new ArrayList<>(targets);
        for (int i = 0; i < instances.size() - 1; i++) {
            String tableLeft = RelationalSQL.table(RelationalSQL.tableContext(instances.get(i).getType()));
            for (int j = i + 1; j < instances.size(); j++) {
                String tableRight = RelationalSQL.table(RelationalSQL.tableContext(instances.get(j).getType()));
                String info = tableLeft + '>' + tableRight + "@";
                if (joinMap.keySet().stream().anyMatch(item -> item.startsWith(info))) {
                    SIComposite newLeft = instances.get(j);
                    instances.remove(j);
                    instances.add(i, newLeft);
                    i--;
                    break;
                }
            }
        }
        targets.clear();
        targets.addAll(instances);
    }

    private void executeManyToManyInsert(SIComposite sourceInstance, SIComposite targetInstance,
            String manyToManyTable) {
        FormKeyRelational sourceKey = (FormKeyRelational) FormKey.fromInstance(sourceInstance);
        FormKeyRelational targetKey = (FormKeyRelational) FormKey.fromInstance(targetInstance);
        StringBuilder sqlBuilder = new StringBuilder("INSERT INTO ");
        sqlBuilder.append(manyToManyTable);
        sqlBuilder.append('(');
        sqlBuilder.append(targetInstance.getParent().asSQL().getManyToManySourceKeyColumns());
        sqlBuilder.append(", ");
        sqlBuilder.append(targetInstance.getParent().asSQL().getManyToManyTargetKeyColumns());
        sqlBuilder.append(") VALUES (");
        List<Object> params = new ArrayList<>();
        String delim = "";
        for (String pkColumn : RelationalSQL.tablePK(sourceInstance.getType())) {
            params.add(sourceKey.getColumnValue(pkColumn));
            sqlBuilder.append(delim);
            sqlBuilder.append('?');
            delim = ", ";
        }
        for (String pkColumn : RelationalSQL.tablePK(targetInstance.getType())) {
            params.add(targetKey.getColumnValue(pkColumn));
            sqlBuilder.append(delim);
            sqlBuilder.append('?');
            delim = ", ";
        }
        sqlBuilder.append(')');
        db.exec(sqlBuilder.toString(), params);
    }

    private String manyToManyTable(RelationalSQLCommmand command) {
        return manyToManyTable(command.getInstance());
    }

    private String manyToManyTable(SIComposite instance) {
        if (instance.getParent() == null) {
            return null;
        }
        return instance.getParent().asSQL().getManyToManyTable();
    }

    protected void updateInternal(@Nonnull SIComposite instance, SIComposite previousPersistedInstance,
            Integer inclusionActor) {
        for (SInstance field : instance.getAllChildren()) {
            if (RelationalSQL.isListWithTableBound(field.getType())) {
                updateFieldInternal(instance, previousPersistedInstance, inclusionActor, field);
            }
        }
        if (execScript(RelationalSQL.update(instance, previousPersistedInstance).toSQLScript()) == 0) {
            throw new SingularFormNotFoundException(FormKey.fromInstance(instance));
        }
    }

    protected void updateFieldInternal(@Nonnull SIComposite instance, SIComposite previousPersistedInstance,
            Integer inclusionActor, SInstance field) {
        SIList<SIComposite> listInstance = instance.getFieldList(field.getType().getNameSimple(), SIComposite.class);
        SIList<SIComposite> previousListInstance = previousPersistedInstance
                .getFieldList(field.getType().getNameSimple(), SIComposite.class);
        for (SIComposite item : listInstance.getChildren()) {
            if (FormKey.containsKey(item))
                updateInternal(instance, locate(FormKey.fromInstance(item), previousListInstance), inclusionActor);
            else
                insertInternal(instance, inclusionActor);
        }
        for (SIComposite item : detectIntancesToDelete(field.getType(), instance, previousPersistedInstance)) {
            deleteInternal(item.getType(), FormKey.fromInstance(item));
        }
    }

    private SIComposite locate(FormKey key, SIList<SIComposite> previousListInstance) {
        String keyString = key.toStringPersistence();
        for (SIComposite item : previousListInstance) {
            if (FormKey.fromInstance(item).toStringPersistence().equals(keyString)) {
                return item;
            }
        }
        return null;
    }

    private Collection<SIComposite> detectIntancesToDelete(SType<?> listField, SIComposite instance,
            SIComposite previousInstance) {
        String fieldName = listField.getNameSimple();
        Map<String, SIComposite> previousKeys = new HashMap<>();
        for (SIComposite item : previousInstance.getFieldList(fieldName, SIComposite.class).getChildren()) {
            previousKeys.put(FormKey.fromInstance(item).toStringPersistence(), item);
        }
        for (SIComposite item : instance.getFieldList(fieldName, SIComposite.class).getChildren()) {
            Optional<FormKey> keyToPreserve = FormKey.fromInstanceOpt(item);
            if (keyToPreserve.isPresent()) {
                previousKeys.remove(keyToPreserve.get().toStringPersistence());
            }
        }
        return previousKeys.values();
    }

    protected int deleteInternal(STypeComposite<?> type, FormKey formKey) {
        return execScript(RelationalSQL.delete(type, formKey).toSQLScript());
    }

    protected List<INSTANCE> executeSelectCommand(RelationalSQLCommmand command) {
        return db.query(command.getSQL(), command.getParameters(), command.getLimitOffset(), command.getLimitRows(),
                rs -> {
                    INSTANCE instance = createInstance();
                    command.setInstance(instance);
                    FormKey.setOnInstance(instance, tupleKey(rs, RelationalSQL.tablePK(instance.getType())));
                    RelationalSQL.persistenceStrategy(instance.getType()).load(instance, tuple(rs, command));
                    return instance;
                });
    }

    protected List<SIComposite> executeSelectCommandIntoSIList(RelationalSQLCommmand command,
            SIList<SIComposite> listInstance) {
        return db.query(command.getSQL(), command.getParameters(), command.getLimitOffset(), command.getLimitRows(),
                rs -> {
                    SIComposite instance = listInstance.addNew();
                    command.setInstance(instance);
                    List<String> pk = RelationalSQL.tablePK(RelationalSQL.tableContext(instance.getType()));
                    FormKey.setOnInstance(instance, tupleKey(rs, pk));
                    RelationalSQL.persistenceStrategy(instance.getType()).load(instance, tuple(rs, command));
                    return instance;
                });
    }

    protected int executeInsertCommand(RelationalSQLCommmand command) {
        List<String> pk = RelationalSQL.tablePK(RelationalSQL.tableContext(command.getInstance().getType()));
        HashMap<String, Object> key = new LinkedHashMap<>();
        pk.forEach(columnName -> key.put(columnName, parameterValue(columnName, command)));
        List<String> generatedColumns = serverSideGeneratedPKColumns(pk, command);
        int result = db.execReturningGenerated(command.getSQL(), command.getParameters(), generatedColumns, rs -> {
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                key.put(generatedColumns.get(i), rs.getObject(i + 1));
            }
            return null;
        });
        FormKey.setOnInstance(command.getInstance(), new FormKeyRelational(key));
        return result;
    }

    protected FormKey tupleKey(ResultSet rs, List<String> pk) throws SQLException {
        HashMap<String, Object> key = new LinkedHashMap<>();
        for (String keyColumn : pk) {
            key.put(keyColumn, rs.getObject(keyColumn));
        }
        return new FormKeyRelational(key);
    }

    protected List<RelationalData> tuple(ResultSet rs, RelationalSQLCommmand command) throws SQLException {
        List<RelationalData> tuple = new ArrayList<>();
        int index = 1;
        for (RelationalColumn column : command.getColumns()) {
            SInstance tupleKeyRef = null;
            if (!RelationalSQL.table(RelationalSQL.tableContext(command.getTupleKeyRef().getType()))
                    .equalsIgnoreCase(column.getTable())) {
                tupleKeyRef = getTupleKeyRef(command.getTupleKeyRef(), column);
            }
            if (tupleKeyRef == null) {
                tupleKeyRef = command.getTupleKeyRef();
            }
            tuple.add(new RelationalData(column.getTable(), tupleKeyRef, column.getName(), column.getSourceKeyColumns(),
                    rs.getObject(index)));
            index++;
        }
        return tuple;
    }

    private SInstance getTupleKeyRef(SInstance instance, RelationalColumn column) {
        if (instance.getType().isComposite()) {
            for (SInstance field : ((SIComposite) instance).getAllChildren()) {
                String fieldTable = RelationalSQL.table(RelationalSQL.tableContext(field.getType()));
                String fieldColunm = RelationalSQL.column(field.getType());
                if (fieldTable != null && fieldColunm != null && fieldTable.equalsIgnoreCase(column.getTable())
                        && fieldColunm.equalsIgnoreCase(column.getName())) {
                    return RelationalSQL.tupleKeyRef(field);
                }
            }
            for (SInstance field : ((SIComposite) instance).getAllChildren()) {
                SInstance current = getTupleKeyRef(field, column);
                if (current != null) {
                    return current;
                }
            }
        }
        return null;
    }

    protected int execScript(Collection<? extends RelationalSQLCommmand> script) {
        int result = 0;
        for (RelationalSQLCommmand command : script) {
            result += db.exec(command.getSQL(), command.getParameters());
        }
        return result;
    }

    protected List<String> serverSideGeneratedPKColumns(List<String> pk, RelationalSQLCommmand command) {
        List<String> result = new ArrayList<>();
        pk.forEach(columnName -> {
            if (parameterValue(columnName, command) == null) {
                result.add(columnName);
            }
        });
        return result;
    }

    private Object parameterValue(String columnName, RelationalSQLCommmand command) {
        Object result = null;
        int paramIndex = 0;
        for (RelationalColumn column : command.getColumns()) {
            if (column.getName().equals(columnName)) {
                result = command.getParameters().get(paramIndex);
                break;
            }
            paramIndex++;
        }
        return result;
    }
}
