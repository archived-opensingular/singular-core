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

package org.opensingular.lib.wicket.util.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.json.JSONObject;
import org.opensingular.lib.wicket.util.multiselect.ChosenOptions;


public class BSSelectInitBehaviour extends InitScriptBehaviour {
    private final ChosenOptions chosenOptions;

    public BSSelectInitBehaviour(ChosenOptions chosenOptions) {
        this.chosenOptions = chosenOptions;
    }

    @Override
    public String getScript(Component component) {
        JSONObject config = new JSONObject();
        config.put("disable_search", chosenOptions.isDisableSearch());
        config.put("no_results_text", chosenOptions.getNoResultsText());
        config.put("placeholder_text_multiple", chosenOptions.getDataPlaceholder());
        config.put("placeholder_text_single", chosenOptions.getDataPlaceholder());
        config.put("disable_search_threshold", chosenOptions.getDisableSearchThreshold());
        config.put("hide_results_on_select", chosenOptions.isHideResultsOnSelect());
        config.put("width", chosenOptions.getWidth());
        return String.format("$('#%s').chosen(%s);", component.getMarkupId(), config.toString());
    }
}