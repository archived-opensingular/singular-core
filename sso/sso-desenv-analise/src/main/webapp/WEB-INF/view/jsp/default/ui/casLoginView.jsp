<%--suppress HtmlUnknownTarget,JspAbsolutePathInspection,ELValidationInJSP --%>
<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%
    response.setHeader("Access-Control-Allow-Origin", "*");
%>
<!DOCTYPE html>
<!--[if IE 8]>
<html lang="en" class="ie8 no-js" xmlns:wicket="http://git-wip-us.apache.org/repos/asf/wicket/repo?p=wicket.git;a=blob_plain;f=wicket-core/src/main/resources/META-INF/wicket-1.5.xsd"> <![endif]-->
<!--[if IE 9]>
<html lang="en" class="ie9 no-js" xmlns:wicket="http://git-wip-us.apache.org/repos/asf/wicket/repo?p=wicket.git;a=blob_plain;f=wicket-core/src/main/resources/META-INF/wicket-1.5.xsd"> <![endif]-->
<!--[if !IE]><!-->
<html class="no-js"
      lang="en">
<head>
    <meta charset="utf-8">
    <title>
        Singular | Login
    </title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <meta http-equiv="Content-type" content="text/html; charset=UTF-8">
    <script src="/singular-static/resources/comum/plugins/jquery.min.js" type="text/javascript"></script>
    <script src="/singular-static/resources/comum/scripts/singular-resource-handler.js" type="text/javascript"></script>

</head>

<body class="login">

<script>
    SingularResourceHandler
            .addStyle('/global/plugins/font-awesome/css/font-awesome.min.css')
            .addStyle('/global/plugins/simple-line-icons/simple-line-icons.min.css')
            .addStyle('/global/plugins/bootstrap/css/bootstrap.min.css')
            .addStyle('/global/plugins/uniform/css/uniform.default.css')
            .addStyle('/global/css/components-rounded.css')
            .addStyle('/global/css/plugins.css')
            .addStyle('/layout4/css/layout.css')
            .addStyle('/layout4/css/themes/default.css')
            .addScript("/global/scripts/app.js")
            .addScript("/global/plugins/respond.min.js")
            .addScript("/global/plugins/excanvas.min.js")
            .addScript("/global/plugins/jquery-migrate.min.js")
            .addScript("/global/plugins/bootstrap/js/bootstrap.min.js")
            .addScript("/global/plugins/jquery.blockui.min.js")
            .addScript("/global/plugins/uniform/jquery.uniform.min.js")
            .addScript("/global/plugins/jquery.cokie.min.js")
            .addScript("/global/plugins/jquery-validation/js/jquery.validate.min.js")
            .addScript("/global/scripts/app.js")
            .addScript("/layout4/scripts/layout.js")
            .setFavicon("/singular-static/resources/singular/img/favicon.png")
            .apply();
</script>

<div class="menu-toggler sidebar-toggler">
</div>

<div class="logo">
    <a>
        <img alt="logo" class="logo-default" id="brandLogo3"
             src="/singular-static/resources/singular/img/logo_singular_gray.svg">
    </a>
</div>


<div class="content">

    <form:form id="form4" method="post" commandName="${commandName}" htmlEscape="true"
               cssClass="login-form"
               novalidate="">
        <h3 class="form-title">Login Análise</h3>
        <form:errors path="*" id="loginErrorC" element="div"
                     cssClass="alert alert-danger input-error"/>
        <div class="alert alert-danger display-hide">
            <button class="close" data-close="alert"></button>
            <span>Preencha o usuário e a senha.</span>
        </div>
        <div class="form-group">

            <label class="control-label visible-ie8 visible-ie9">Usuário</label>

            <form:input
                    name="email"
                    autocomplete="on"
                    maxlength="60"
                    placeholder="Usuário"
                    cssClass="form-control form-control-solid placeholder-no-fix"
                    id="username" tabindex="1"
                    accesskey="${userNameAccessKey}" path="username"
                    htmlEscape="true"
            />
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">Senha</label>
            <form:password
                    cssClass="form-control form-control-solid placeholder-no-fix"
                    name="senha"
                    autocomplete="on"
                    placeholder="Senha"
                    id="password" tabindex="2" path="password"
                    accesskey="${passwordAccessKey}" htmlEscape="true"
                    maxlength="60"
            />
        </div>
        <input type="hidden" name="lt" value="${loginTicket}"/>
        <input type="hidden" name="execution" value="${flowExecutionKey}"/>
        <input type="hidden" name="_eventId" value="submit"/>
        <div class="form-actions">
            <button id="btnsubss" type="submit" class="btn uppercase">Login</button>
        </div>

    </form:form>

</div>
<div class="copyright">
    2016 © Singular
    <a target="_blank" id="ownerLink7" href="http://www.opensingular.org"
       title="Soluções Inteligentes para criação de petição e análise.">
        Singular.
    </a>
</div>


<script>
    /*<![CDATA[*/

    jQuery(document).ready(function () {
        App.init(); // init metronic core components
        Layout.init(); // init current layout
        $("input").keyup(function (event) {
            if (event.keyCode == 13) {
                $("#btnsubss").click();
            }
        });
    });
    /*]]>*/
</script>


</body>
<!--[if IE 8]> </html> <![endif]--><!--[if IE 9]> </html> <![endif]--><!--[if !IE]><!--></html>