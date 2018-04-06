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
        create: function (params) {
            var selectOnlyLeaf = params.onlyLeafSelected;
            var showOnlyMatchesChildren = params.showOnlyMatchesChildren;
            var showOnlyMatches = params.showOnlyMatches;
            var hidden = params.hidden;
            var results = params.data;
            var tree = $('#tree');
            tree.jstree({
                'conditionalselect': function (node) {
                    return (this.is_leaf(node) && selectOnlyLeaf) || (!selectOnlyLeaf);
                },
                'core': {
                    'dblclick_toggle': false,
                    'data': results
                },
                'types': {
                    "leaf": {
                        "icon": "fa fa-file-text"
                    },
                    "open" : {
                        "icon" : "jstree-icon jstree-themeicon fa fa-folder icon-state-warning icon-lg jstree-themeicon-custom"
                    },
                    "close" : {
                        "icon" : "jstree-icon jstree-themeicon fa fa-folder icon-state-warning icon-lg jstree-themeicon-custom"
                    }
                },
                'search' : {
                    "show_only_matches" : showOnlyMatches,
                    "show_only_matches_children" : showOnlyMatchesChildren
                },
                'plugins': ["types", "search", "conditionalselect"]
            });
            tree.on('click', '.jstree-anchor', function (e) {
                tree.jstree(true).toggle_node(e.target);
            });
            tree.on("select_node.jstree", function (e, data) {
                var formatId = hidden.replace(/'/g, "");
                $('#'+formatId).val(data.node.id);
            });
            tree.on("open_node.jstree", function (event, data) {
                data.instance.set_type(data.node,'open');
            });
            tree.on("close_node.jstree", function (event, data) {
                data.instance.set_type(data.node,'close');
            });
        },

        find: function (value) {
            var tree = $('#tree');
            var treeImpl = tree.jstree(true);
            treeImpl.search(value);
            var result = tree.find('.jstree-search');
            if (result.length === 1) {
                treeImpl.select_node(result.attr("id"));
            }
        }
    }
}();
