package br.net.mirante.singular.form.mform.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

public interface IMInstanceListener extends Serializable {

    void onInstanceEvent(MInstanceEvent evt);

    public static class EventCollector implements IMInstanceListener {
        private final List<MInstanceEvent> events = new ArrayList<>();
        private Predicate<MInstanceEvent>  filter;
        public EventCollector() {
            this.filter = e -> true;
        }
        public EventCollector(Predicate<MInstanceEvent> filter) {
            this.filter = filter;
        }
        @Override
        public void onInstanceEvent(MInstanceEvent evt) {
            if (filter.test(evt)) {
                events.add(evt);
            }
        }
        public List<MInstanceEvent> getEvents() {
            return ImmutableList.copyOf(events);
        }
        public void clear() {
            this.events.clear();
        }
    }
}
