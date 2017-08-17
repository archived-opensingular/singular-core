package org.opensingular.form.flatview;

import org.opensingular.form.view.SView;

public class SViewQualifier {
    private Class<? extends SView> view;

    public SViewQualifier(Class<? extends SView> view) {
        this.view = view;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SViewQualifier that = (SViewQualifier) o;

        return view != null ? view.equals(that.view) : that.view == null;
    }

    @Override
    public int hashCode() {
        return view != null ? view.hashCode() : 0;
    }
}
