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

package org.opensingular.form.event;

import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface ISInstanceListener {

    void onInstanceEvent(SInstanceEvent evt);

    public static class EventCollector implements ISInstanceListener {

        private List<SInstanceEvent> events;

        private final Predicate<SInstanceEvent> filter;

        public EventCollector() {
            filter = null;
        }

        public EventCollector(Predicate<SInstanceEvent> filter) {
            this.filter = filter;
        }

        @Override
        public void onInstanceEvent(SInstanceEvent evt) {
            if (filter == null || filter.test(evt)) {
                if (events == null) {
                    events = new ArrayList<>();
                }
                events.add(evt);
            }
        }

        public Stream<SInstanceEvent> streamEvents() {
            return events == null ? Stream.empty() : events.stream();
        }

        public List<SInstanceEvent> getEvents() {
            return events == null ? Collections.emptyList() : ImmutableList.copyOf(events);
        }

        public void clear() {
            this.events = null;
        }
    }
}
