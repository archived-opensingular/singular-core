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

(function () {
    "use strict";


    var $tabMenuActive = $('.tab-pane.active .container-tabMenu-active'),
        $containerActive = $('.tab-pane.active .container-content-active'),
        $navHeader = $('.page-header.navbar.navbar-fixed-top'),
        offsetTop = ($tabMenuActive.offset().top);
    var width;
    var NAV_PADDING_SCROLL = 15;
    var differenceTopNavWhenScroll;
    var componentToFixPosition;
    configureInitialize();

    $(window).scroll(togglePosition);
    Wicket.Event.subscribe("/ajax/call/complete", togglePosition);

    $(window).resize(function () {
        configureInitialize();
        configureOffSetTop();
        configureWidth();
        togglePosition();
    });

    configureWidth();

    function isBellowPageHeader() {
        return differenceTopNavWhenScroll <= $navHeader.height()
    }

    function isLessThenContent() {
        return (differenceTopNavWhenScroll * -1) <= $containerActive.height() - componentToFixPosition;
    }

    function getTheEndOfContent() {
        return $navHeader.height() + NAV_PADDING_SCROLL - ((differenceTopNavWhenScroll * -1) - ($containerActive.height() - componentToFixPosition));
    }


    function configureInitialize() {
        if ($tabMenuActive.attr('id') != $('.tab-pane.active .container-tabMenu-active').attr('id')) {
            $tabMenuActive = $('.tab-pane.active .container-tabMenu-active');
            $containerActive = $('.tab-pane.active .container-content-active');
            configureOffSetTop();
            configureWidth();
        }

    }

    function configureOffSetTop() {
        offsetTop = ($tabMenuActive.offset().top);
    }

    function configureWidth() {
        width = $tabMenuActive.parent().width();
    }

    function togglePosition() {
        configureInitialize();

        differenceTopNavWhenScroll = offsetTop - $(window).scrollTop();
        componentToFixPosition = $navHeader.height() + NAV_PADDING_SCROLL + 15 + $tabMenuActive.height();
        if (isBellowPageHeader()) {
            if (isLessThenContent()) {
                $tabMenuActive.css('position', 'fixed');
                $tabMenuActive.css('top', $navHeader.height() + NAV_PADDING_SCROLL);
                $tabMenuActive.css('width', width);
            } else if (getTheEndOfContent() >= 0) {
                $tabMenuActive.css('position', 'fixed');
                $tabMenuActive.css('top', getTheEndOfContent());
                $tabMenuActive.css('width', width);
            } else {
                $tabMenuActive.css('position', 'relative');
                $tabMenuActive.css('top', 'auto');
                $tabMenuActive.css('width', 'auto');
            }
        } else {
            $tabMenuActive.css('position', 'relative');
            $tabMenuActive.css('top', 'auto');
            $tabMenuActive.css('width', 'auto');
        }
    }

}());