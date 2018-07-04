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

import org.opensingular.lib.commons.ui.Icon;

public enum DefaultIcons implements Icon {

    ARCHIVE("fa fa-archive"),
    ARROW_RIGHT("fa fa-arrow-right"),
    BAN("icon-ban"),
    BARCODE("fa fa-barcode"),
    BRIEFCASE("icon-briefcase"),
    BUG("fa fa-bug"),
    CALENDAR("icon-calendar"),
    CALENDAR_PLUS_O("fa fa-calendar-plus-o"),
    CAMERA("icon-camera"),
    CARET_SQUARE("fa fa-caret-square-o-up"),
    CHAIN("fa fa-chain"),
    CHECK("fa fa-check"),
    CHECK_CIRCLE("fa-check-circle-o"),
    CLOCK("icon-clock"),
    CLONE("fa fa-clone"),
    CODE("fa fa-code"),
    COG("fa fa-cog"),
    COGS("fa fa-cogs"),
    COMMENT("fa fa-comment"),
    COUNTRY("icon-globe"),
    CREDIT_CARD("icon-credit-card"),
    CUBES(" fa fa-cubes "),
    CUP("icon-cup"),
    DASHBOARD("fa fa-dashboard"),
    DIRECTIONS("icon-directions"),
    DOCS("icon-docs"),
    EXCLAMATION_TRIANGLE("fa fa-exclamation-triangle"),
    EXTERNAL_LINK("fa fa-external-link"),
    EYE("icon-eye"),
    FILE_PDF("fa fa-file-pdf-o"),
    FILE_POWERPOINT("fa fa-file-powerpoint-o"),
    FILE_TEXT("fa fa-file-text"),
    FOLDER("icon-folder"),
    FOLDER_OPEN("fa fa-folder-open-o"),
    GIFT("fa fa-gift"),
    GLOBE("fa fa-globe"),
    GRID("icon-grid"),
    HAND_UP("fa fa-hand-o-up"),
    HEART("fa fa-heart"),
    HELP("fa fa-question-circle"),
    HISTORY("fa fa-history"),
    HOME("icon-home"),
    HOTEL("fa fa-h-square"),
    HOURGLASS("icon-hourglass"),
    INBOX("fa fa-inbox"),
    INFO_CIRCLE("fa fa-info-circle"),
    LAYERS("icon-layers"),
    LINE_CHART("fa fa-line-chart"),
    LIST("fa fa-list"),
    LIST_ALT("fa fa-list-alt"),
    LOCK("fa fa-lock"),
    MAGIC("fa fa-magic"),
    MAP("icon-map"),
    MAP_MARKER("fa fa-map-marker"),
    MINUS("fa fa-minus"),
    MONEY("fa fa-money"),
    NEWSPAPER("fa fa-newspaper-o"),
    NOTE("icon-note"),
    PENCIL("fa fa-pencil"),
    PENCIL_SQUARE("fa fa-pencil-square-o"),
    PIE("icon-pie-chart"),
    PIN("icon-pin"),
    PLUS("fa fa-plus"),
    PUZZLE("icon-puzzle"),
    QUESTION_CIRCLE("fa fa-question-circle"),
    RECYCLE("fa fa-recycle"),
    REDO("icon-action-redo"),
    REMOVE("fa fa-remove"),
    ROCKET("icon-rocket"),
    SEARCH("fa fa-search"),
    SEND_O("fa fa-send-o"),
    SHARE_ALT("fa fa-share-alt"),
    SHARE_SQUARE("fa fa-share-square-o"),
    SITEMAP("fa fa-sitemap"),
    SPEECH("icon-speech"),
    SPEEDOMETER("icon-speedometer"),
    STAR("icon-star"),
    TAG("icon-tag"),
    TAGS("fa fa-tags"),
    TASKS("fa fa-tasks"),
    TIMES("fa fa-times"),
    TRASH("fa fa-trash-o "),
    THUMB_DOWN("fa fa-thumbs-o-down"),
    UNDO("icon-action-undo"),
    UPLOAD("fa fa-upload"),
    USER("fa fa-user"),
    USERS("icon-users"),
    USERS3("fa fa-users"),
    VERTICAL_ELLIPSIS("fa fa-ellipsis-v"),
    WALLET("icon-wallet"),
    WARNING("fa fa-warning"),
    WRENCH("icon-wrench"),
    ARROW_DOWN("fa fa-arrow-down"),
    FLOPPY("fa fa-floppy-o");

    private final String cssClass;

    DefaultIcons(String cssClass) {
        this.cssClass = cssClass;
    }

    @Override
    public String getCssClass() {
        return cssClass;
    }

    @Override
    public String toString() {
        return getCssClass();
    }
}
