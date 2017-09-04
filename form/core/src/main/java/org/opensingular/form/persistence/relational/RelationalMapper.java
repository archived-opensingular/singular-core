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

package org.opensingular.form.persistence.relational;

import java.util.List;

import org.opensingular.form.SType;
import org.opensingular.form.aspect.AspectRef;
import org.opensingular.form.persistence.relational.strategy.PersistenceStrategy;

/**
 * Mapper interface for persisting Form components into a Relational DBMS.
 *
 * @author Edmundo Andrade
 */
public interface RelationalMapper {
	public static final AspectRef<RelationalMapper> ASPECT_RELATIONAL_MAP = new AspectRef<>(RelationalMapper.class);

	String table(SType<?> type);

	List<String> tablePK(SType<?> type);

	List<RelationalFK> tableFKs(SType<?> field);

	String column(SType<?> field);

	PersistenceStrategy persistenceStrategy(SType<?> type);
}
