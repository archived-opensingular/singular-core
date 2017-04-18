/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.wicket.util.resource;

public enum Icone {
    //@formatter:off
    ARROW_DOWN          ("fa fa-arrow-down"),
    BAN                 ("icon-ban"),
    BRIEFCASE           ("icon-briefcase"),
    BUG                 ("fa fa-bug"),
    CALENDAR            ("icon-calendar"),
    CAMERA              ("icon-camera"),
    CARET_SQUARE        ("fa fa-caret-square-o-up"),
    CHAIN               ("fa fa-chain"),
    CHECK               ("fa fa-check"),
    CHECK_CIRCLE        ("fa-check-circle-o"),
    COGS                ("fa fa-cogs"),
    COMMENT             ("fa fa-comment"),
    CREDIT_CARD         ("icon-credit-card"),
    CUP                 ("icon-cup"),
    DIRECTIONS          ("icon-directions"),
    EXTERNAL_LINK       ("fa fa-external-link"),
    EYE                 ("icon-eye"),
    FILE_POWERPOINT     ("fa fa-file-powerpoint-o"),
    FILE_PDF            ("fa fa-file-pdf-o"),
    FILE_TEXT           ("fa fa-file-text"),
    GIFT                ("fa fa-gift"),
    GLOBE               ("fa fa-globe"),
    GRID                ("icon-grid"),
    HEART               ("fa fa-heart"),
    HISTORY             ("fa fa-history"),
    HOME                ("icon-home"),
    HOTEL               ("fa fa-h-square"),
    HOURGLASS           ("icon-hourglass"),
    INFO_CIRCLE         ("fa fa-info-circle"),
    LIST                ("fa fa-list"),
    LIST_ALT            ("fa fa-list-alt"),
    LOCK                ("fa fa-lock"),
    MAP_MARKER          ("fa fa-map-marker"),
    MINUS               ("fa fa-minus"),
    MONEY               ("fa fa-money"),
    PENCIL_SQUARE       ("fa fa-pencil-square-o"),
    PENCIL              ("fa fa-pencil"),
    PIN                 ("icon-pin"),
    PIE                 ("icon-pie-chart"),
    PLUS                ("fa fa-plus"),
    ROCKET              ("icon-rocket"),
    REDO                ("icon-action-redo"),
    REMOVE              ("fa fa-remove"),
    SHARE_ALT           ("fa fa-share-alt"),
    SHARE_SQUARE        ("fa fa-share-square-o"),
    STAR                ("icon-star"),
    SPEECH              ("icon-speech"),
    SPEEDOMETER         ("icon-speedometer"),
    TAG                 ("icon-tag"),
    TAGS                ("fa fa-tags"),
    TIMES               ("fa fa-times"),
    TRASH               ("fa fa-trash-o "),
    UNDO                ("icon-action-undo"),
    USER                ("fa fa-user"),
    USERS               ("icon-users"),
    USERS3              ("fa fa-users"),
    VERTICAL_ELLIPSIS   ("fa fa-ellipsis-v"),
    WALLET              ("icon-wallet"),
    PUZZLE              ("icon-puzzle"),
    FOLDER              ("icon-folder"),
    WRENCH              ("icon-wrench"),
    MAP                 ("icon-map"),
    NOTE                ("icon-note"),
    DOCS                ("icon-docs"),
    CLOCK               ("icon-clock"),
    LAYERS              ("icon-layers"),
    CODE                ("fa fa-code"),
    HAND_UP             ("fa fa-hand-o-up"),
    DASHBOARD           ("fa fa-dashboard"),
    EXCLAMATION_TRIANGLE("fa fa-exclamation-triangle"),
    MAGIC               ("fa fa-magic"),
    CLONE               ("fa fa-clone"),
    UPLOAD              ("fa fa-upload"),
    BARCODE             ("fa fa-barcode"),
    SEND_O              ("fa fa-send-o"),
    CALENDAR_PLUS_O     ("fa fa-calendar-plus-o"),
    RECYCLE             ("fa fa-recycle")

    ;


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
