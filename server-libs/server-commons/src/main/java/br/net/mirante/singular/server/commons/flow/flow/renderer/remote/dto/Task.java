package br.net.mirante.singular.server.commons.flow.flow.renderer.remote.dto;

import java.util.List;

public class Task {

    private boolean wait;

    private boolean java;

    private boolean people;

    private boolean end;
    private String name;
    private String abbreviation;
    private boolean start;
    private List<Transition> transitions;
    private boolean sendEmail;

    public Task(boolean wait, boolean java, boolean people, boolean end, String name, String abbreviation, boolean start, List<Transition> transitions, boolean sendEmail) {
        this.wait = wait;
        this.java = java;
        this.people = people;
        this.end = end;
        this.name = name;
        this.abbreviation = abbreviation;
        this.start = start;
        this.transitions = transitions;
        this.sendEmail = sendEmail;
    }

    public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }

    public boolean isJava() {
        return java;
    }

    public void setJava(boolean java) {
        this.java = java;
    }

    public boolean isPeople() {
        return people;
    }

    public void setPeople(boolean people) {
        this.people = people;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public List<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<Transition> transitions) {
        this.transitions = transitions;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }
}
