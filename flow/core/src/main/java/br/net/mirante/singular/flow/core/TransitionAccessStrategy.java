package br.net.mirante.singular.flow.core;

import com.google.common.base.Preconditions;

@FunctionalInterface
public interface TransitionAccessStrategy<T extends TaskInstance> {

    TransitionAccess getAccess(T taskInstance);

    public enum TransitionAccessLevel {
        ENABLED,
        ENABLED_BUT_HIDDEN,
        DISABLED_BUT_VISIBLE,
        DISABLED_AND_HIDDEN;
    }

    public static class TransitionAccess {
        private final TransitionAccessLevel level;
        private final String message;

        public TransitionAccess(TransitionAccessLevel transitionAccessLevel, String message) {
            Preconditions.checkNotNull(transitionAccessLevel);
            this.level = transitionAccessLevel;
            this.message = message;
        }

        public boolean isEnabled() {
            return level.equals(TransitionAccessLevel.ENABLED) || level.equals(TransitionAccessLevel.ENABLED_BUT_HIDDEN);
        }

        public boolean isVisible() {
            return level.equals(TransitionAccessLevel.ENABLED) || level.equals(TransitionAccessLevel.DISABLED_BUT_VISIBLE);
        }

        public String getMessage() {
            return message;
        }
    }

    public static <T extends TaskInstance> TransitionAccessStrategy<T> enabled(boolean enabled) {
        return (instance) -> {
            if (enabled) {
                return new TransitionAccess(TransitionAccessLevel.ENABLED, null);
            } else {
                return new TransitionAccess(TransitionAccessLevel.DISABLED_AND_HIDDEN, "Unauthorized action");
            }
        };
    }

    public static <T extends TaskInstance> TransitionAccessStrategy<T> sameStrategyOf(final MTask<?> task, boolean visible) {
        return (instance) -> {
            MUser user = MBPM.getUserSeDisponivel();
            boolean canExecute = user != null && task.getAccessStrategy().canExecute(instance, user);
            if (canExecute && visible) {
                return new TransitionAccess(TransitionAccessLevel.ENABLED, null);
            } else if (canExecute && !visible) {
                return new TransitionAccess(TransitionAccessLevel.ENABLED_BUT_HIDDEN, null);
            } else if (!canExecute && visible) {
                return new TransitionAccess(TransitionAccessLevel.DISABLED_BUT_VISIBLE, "Unauthorized action");
            } else {
                return new TransitionAccess(TransitionAccessLevel.DISABLED_AND_HIDDEN, "Unauthorized action");
            }
        };
    }

}
