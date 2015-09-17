package br.net.mirante.singular.dsl;

public class TransitionBuilder1 {

    TaskBuilder2 taskBuilder2;

    public TransitionBuilder1(TaskBuilder2 taskBuilder2) {
        this.taskBuilder2 = taskBuilder2;

    }

    public TransitionBuilder1(TaskBuilder taskBuilder) {
        
    }

    public TransitionBuilder1(PeopleBuilder2 peopleBuilder2) {
    }

    public TransitionBuilder1(WaitBuilder2 waitBuilder2) {

    }

    public TaskBuilder2 goTo(String task){
        return taskBuilder2;
    }

    public TaskBuilder2 go() {
        return taskBuilder2;
    }

    public PeopleBuilder2 gotTo(String s) {
        return new PeopleBuilder2(this);
    }
}
