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

package org.opensingular.server.commons.flow.renderer.remote.dto;


import org.opensingular.flow.core.EventType;

public class Transition {
    private TransitionTask origin;
    private TransitionTask destination;
    private String name;
    private EventType predicate;

    public Transition(TransitionTask origin, TransitionTask destination, String name, EventType predicate) {
        this.origin = origin;
        this.destination = destination;
        this.name = name;
        this.predicate = predicate;
    }

    public TransitionTask getOrigin() {
        return origin;
    }

    public void setOrigin(TransitionTask origin) {
        this.origin = origin;
    }

    public TransitionTask getDestination() {
        return destination;
    }

    public void setDestination(TransitionTask destination) {
        this.destination = destination;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventType getPredicate() {
        return predicate;
    }

    public void setPredicate(EventType predicate) {
        this.predicate = predicate;
    }
}
