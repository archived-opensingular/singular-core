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
var treeView = function () {
    //noinspection JSUnusedGlobalSymbols
    return {
        create: function (data, callbackUrl) {
            $('#tree').jstree({
                'core': {
                    'dblclick_toggle' : false,
                    'data': data
                },
                'types': {
                    "leaf": {
                        "icon": "fa fa-file-text"
                    }
                },
                'plugins': ["types", "search"]
            });
            $('#tree').on("changed.jstree", function (e, data) {
                data.instance.toggle_node(data.node);
            });
            $('#tree').bind("dblclick.jstree", function (event) {
                var tree = $(this).jstree();
                var node = tree.get_node(event.target);
                var params = {'id': node.id, 'label': node.text};
                Wicket.Ajax.post({u: callbackUrl, ep: params});
            });
        },

        find: function (value) {
            $("#tree").jstree(true).search(value);
        }
    }
}();
