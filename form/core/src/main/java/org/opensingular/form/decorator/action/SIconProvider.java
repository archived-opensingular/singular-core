package org.opensingular.form.decorator.action;

public interface SIconProvider {
    int order();
    SIcon resolve(String s);
}
