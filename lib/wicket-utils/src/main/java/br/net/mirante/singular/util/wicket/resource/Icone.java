package br.net.mirante.singular.util.wicket.resource;

public enum Icone {
    ARROW_DOWN /*              */("fa fa-arrow-down"),
    BAN /*              */("icon-ban"),
    BRIEFCASE /*        */("icon-briefcase"),
    CALENDAR /*         */("icon-calendar"),
    CAMERA /*           */("icon-camera"),
    CARET_SQUARE /*     */("fa fa-caret-square-o-up"),
    CHAIN /*            */("fa fa-chain"),
    CHECK /*            */("fa fa-check"),
    CHECK_CIRCLE /*     */("fa-check-circle-o"),
    COGS /*             */("fa fa-cogs"),
    COMMENT /*          */("fa fa-comment"),
    CREDIT_CARD /*      */("icon-credit-card"),
    CUP /*              */("icon-cup"),
    DIRECTIONS /*       */("icon-directions"),
    EXTERNAL_LINK /*    */("fa fa-external-link"),
    EYE /*              */("icon-eye"),
    FILE_POWERPOINT /*  */("fa fa-file-powerpoint-o"),
    FILE_PDF /*         */("fa fa-file-pdf-o"),
    FILE_TEXT /*        */("fa fa-file-text"),
    GIFT /*             */("fa fa-gift"),
    GLOBE /*            */("fa fa-globe"),
    GRID /*             */("icon-grid"),
    HEART/*             */("fa fa-heart"),
    HISTORY/*           */("fa fa-history"),
    HOME/*              */("icon-home"),
    HOTEL/*             */("fa fa-h-square"),
    HOURGLASS/*         */("icon-hourglass"),
    INFO_CIRCLE /*      */("fa fa-info-circle"),
    LIST/*              */("fa fa-list"),
    LIST_ALT/*          */("fa fa-list-alt"),
    LOCK/*              */("fa fa-lock"),
    MAP_MARKER /*       */("fa fa-map-marker"),
    MINUS /*            */("fa fa-minus"),
    MONEY /*            */("fa fa-money"),
    PENCIL_SQUARE /*    */("fa fa-pencil-square-o"),
    PENCIL/*            */("fa fa-pencil"),
    PIN /*              */("icon-pin"),
    PIE /*              */("icon-pie-chart"),
    PLUS /*             */("fa fa-plus"),
    ROCKET /*           */("icon-rocket"),
    REDO /*             */("icon-action-redo"),
    SHARE_ALT /*        */("fa fa-share-alt"),
    SHARE_SQUARE /*     */("fa fa-share-square-o"),
    STAR/*              */("icon-star"),
    SPEECH /*           */("icon-speech"),
    SPEEDOMETER /*      */("icon-speedometer"),
    TAG /*              */("icon-tag"),
    TAGS /*             */("fa fa-tags"),
    TIMES /*            */("fa fa-times"),
    UNDO /*             */("icon-action-undo"),
    USER /*             */("fa fa-user"),
    USERS /*            */("icon-users"),
    USERS3 /*           */("fa fa-users"),
    VERTICAL_ELLIPSIS /**/("fa fa-ellipsis-v"),
    WALLET /*           */("icon-wallet"),
    PUZZLE /*           */("icon-puzzle"),
    FOLDER /*           */("icon-folder"),
    WRENCH /*           */("icon-wrench"),
    MAP /*              */("icon-map"),
    NOTE /*             */("icon-note"),
    DOCS /*             */("icon-docs"),
    CLOCK /*            */("icon-clock"),
    LAYERS/*            */("icon-layers");

    private final String cssClass;

    Icone(String cssClass) {
        this.cssClass = cssClass;
    }

    public String getCssClass() {
        return cssClass;
    }

    @Override
    public String toString() {
        return getCssClass();
    }
}
