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
        create: function (data, hidden, selectOnlyLeaf) {
            $('#tree').jstree({
                'conditionalselect': function (node) {
                    return (this.is_leaf(node) && selectOnlyLeaf) || (!selectOnlyLeaf) ? true : false;
                },
                'core': {
                    'dblclick_toggle': false,
                    'data': data
                },
                'types': {
                    "leaf": {
                        "icon": "fa fa-file-text"
                    }
                },
                'plugins': ["types", "search", "conditionalselect"]
            });
            $('#tree').on("changed.jstree", function (e, data) {
                data.instance.toggle_node(data.node);
            });
            $('#tree').on("select_node.jstree", function (e, data) {
                $('#'+hidden).val(data.node.id);
            });
        },

        find: function (value) {
            $("#tree").jstree(true).search(value);
        }
    }
}();
