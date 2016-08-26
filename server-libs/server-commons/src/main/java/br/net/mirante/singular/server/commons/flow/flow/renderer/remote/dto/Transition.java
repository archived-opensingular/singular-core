package br.net.mirante.singular.server.commons.flow.flow.renderer.remote.dto;


import br.net.mirante.singular.flow.core.EventType;

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
