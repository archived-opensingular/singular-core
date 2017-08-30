package org.opensingular.form.studio;

public interface StudioCRUDPermissionStrategy {
    StudioCRUDPermissionStrategy ALL = new StudioCRUDPermissionStrategy() {
        @Override
        public boolean canCreate() {
            return true;
        }

        @Override
        public boolean canEdit() {
            return true;
        }

        @Override
        public boolean canRemove() {
            return true;
        }

        @Override
        public boolean canView() {
            return true;
        }
    };
    StudioCRUDPermissionStrategy VIEW_ONLY = new StudioCRUDPermissionStrategy() {
        @Override
        public boolean canCreate() {
            return false;
        }

        @Override
        public boolean canEdit() {
            return false;
        }

        @Override
        public boolean canRemove() {
            return false;
        }

        @Override
        public boolean canView() {
            return true;
        }
    };

    boolean canCreate();

    boolean canEdit();

    boolean canRemove();

    boolean canView();
}