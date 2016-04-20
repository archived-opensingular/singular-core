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