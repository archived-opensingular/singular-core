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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;

/**
 * Basic implementation of a Relational Mapper.
 *
 * @author Edmundo Andrade
 */
public class BasicRelationalMapper implements RelationalMapper {
	public String table(SType<?> field) {
		return field.getName().substring(0, field.getName().lastIndexOf('.'));
	}

	public String column(SType<?> field) {
		return field.getNameSimple();
	}

	public List<String> keyColumns(SType<?> field) {
		return Arrays.asList("id");
	}

	public List<RelationalData> data(SInstance fieldInstance) {
		SType<?> field = fieldInstance.getType();
		List<RelationalData> list = new ArrayList<>();
		list.add(new RelationalData(table(field), null, column(field), null));
		return list;
	}
}
