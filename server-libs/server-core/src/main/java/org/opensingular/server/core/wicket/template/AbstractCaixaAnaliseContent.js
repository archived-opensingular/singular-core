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

var AnvisaPetServerPost = function () {
    return {
        postToModule: function (url, type, key, viewMode, annotation) {
            var jw = window.open('',key);

            jw.document.write("<html>");
            jw.document.write("<body>");
            jw.document.write("</body>");
            jw.document.write("</html>");

            var form = jw.document.createElement("form");
            form.setAttribute("action", url);
            form.setAttribute("method", "POST");
            form.setAttribute("accept-charset", "UTF-8");

            var inputkey = jw.document.createElement("input");
            inputkey.setAttribute("type", "hidden");
            inputkey.setAttribute("name", "key");
            inputkey.setAttribute("value", key);
            form.appendChild(inputkey);

            var inputtype = jw.document.createElement("input");
            inputtype.setAttribute("type", "hidden");
            inputtype.setAttribute("name", "type");
            inputtype.setAttribute("value", type);
            form.appendChild(inputtype);

            var inputviewMode = jw.document.createElement("input");
            inputviewMode.setAttribute("type", "hidden");
            inputviewMode.setAttribute("name", "viewMode");
            inputviewMode.setAttribute("value", viewMode);
            form.appendChild(inputviewMode);

            var inputannotation = jw.document.createElement("input");
            inputannotation.setAttribute("type", "hidden");
            inputannotation.setAttribute("name", "annotation");
            inputannotation.setAttribute("value", annotation);
            form.appendChild(inputannotation);

            var button = jw.document.createElement("input");
            button.setAttribute("id", "bbu");
            button.setAttribute("type", "submit");
            button.setAttribute("style", "display:none;");
            form.appendChild(button);

            var body = jw.document.getElementsByTagName("BODY")[0];

            body.appendChild(form);
            button.click();

        }
    }
}();