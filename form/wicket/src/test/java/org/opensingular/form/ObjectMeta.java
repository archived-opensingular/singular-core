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

import net.vidageek.mirror.dsl.Mirror;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ObjectMeta {

    public static class ObjectData {
        public long       id;
        public String     className;
        public Object     value;
        public ObjectData parent;

        public ObjectData(long id, Object o, ObjectData p) {
            this.id = id;
            className = o.getClass().getName();
            value = o;
            parent = p;
        }
    }

    public static void printPaths(List<ObjectMeta.ObjectData> objectData) {
        for (ObjectMeta.ObjectData o : objectData) {
            printPath(o);
            System.out.println("\n\n");
        }
    }

    public static void printPath(ObjectMeta.ObjectData o) {
        if (o != null) {
            printPath(o.parent);
            System.out.println("[" + o.id + "]" + o.value.getClass().getName());
        }
    }

    public static Map<String, List<ObjectData>> hihihi(Object o) throws IllegalAccessException {
        Map<String, List<ObjectMeta.ObjectData>> map  = new TreeMap<>();
        List<Integer>                            ids  = new ArrayList<>();
        ObjectData                               data = add(o, null, map, ids);
        mwhahahah(o, data, map, ids);
        return map;
    }

    private static void mwhahahah(Object o, ObjectData data, Map<String, List<ObjectData>> result, List<Integer> ids) throws IllegalAccessException {
        if (o instanceof Serializable) {
            reflectFields(o, data, result, ids);
            if (o instanceof Iterable) {
                Iterable i = (Iterable) o;
                for (Object o1 : i) {
                    reflectFields(o1, data, result, ids);
                }
            }
        }
    }

    private static void reflectFields(Object o, ObjectData oData, Map<String, List<ObjectData>> result, List<Integer> ids) throws IllegalAccessException {
        for (Field field : new Mirror().on(o.getClass()).reflectAll().fields()) {
            field.setAccessible(true);
            Object value = field.get(o);
            if (value == null || ids.contains(System.identityHashCode(value))) {
                continue;
            }
            ObjectData valueData = add(value, oData, result, ids);
            if (!skipThis(value)) {
                mwhahahah(value, valueData, result, ids);
            }
        }
    }

    private static boolean skipThis(Object value) {
        if (value.getClass().getName().startsWith("java.lang")) {
            return true;
        }
        return false;
    }

    private static ObjectData add(Object o, ObjectData parent, Map<String, List<ObjectData>> result, List<Integer> ids) {
        int id = System.identityHashCode(o);
        ids.add(id);

        List<ObjectData> list = result.get(o.getClass().getName());
        if (list == null) {
            list = new ArrayList<>();
        }

        ObjectData data = new ObjectData(id, o, parent);
        list.add(data);
        result.put(o.getClass().getName(), list);
        return data;
    }
}
