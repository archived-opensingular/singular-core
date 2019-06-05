package org.opensingular.form.wicket.enums;

public enum PageState {

    NOT_SEND,
    SEND;

    public boolean isSend() {
        return this == SEND;
    }

    public boolean isNotSend() {
        return this == NOT_SEND;
    }


}
