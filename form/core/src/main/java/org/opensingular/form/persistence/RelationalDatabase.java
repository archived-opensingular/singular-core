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

import java.util.Collection;
import java.util.List;

import org.opensingular.form.persistence.relational.RelationalSQLCommmand;
import org.opensingular.form.persistence.relational.RelationalTupleHandler;

/**
 * Interface for relational database managers.
 *
 * @author Edmundo Andrade
 */
public interface RelationalDatabase {
	int exec(String sql);

	int exec(String sql, List<Object> params);

	int execReturningGenerated(String sql, List<Object> params, List<String> generatedColumns,
			RelationalTupleHandler<?> tupleHandler);

	int execScript(Collection<? extends RelationalSQLCommmand> script);

	List<Object[]> query(String sql, List<Object> params);

	<T> List<T> query(String sql, List<Object> params, RelationalTupleHandler<T> tupleHandler);

	List<Object[]> query(String sql, List<Object> params, Long limitOffset, Long limitRows);

	<T> List<T> query(String sql, List<Object> params, Long limitOffset, Long limitRows,
			RelationalTupleHandler<T> tupleHandler);
}
