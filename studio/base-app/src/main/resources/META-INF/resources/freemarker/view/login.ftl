<#import "/spring.ftl" as spring>
<!DOCTYPE html>
<!--[if IE 8]><html lang="en" class="ie8 no-js"><![endif]-->
<!--[if IE 9]><html lang="en" class="ie9 no-js"><![endif]-->
<!--[if !IE]><!-->
<html class="no-js" lang="en">
<head>
    <meta charset="utf-8">
    <title>
        Singular Studio | Login
    </title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta content="width=device-width, initial-scale=1.0" name="viewport">
    <meta http-equiv="Content-type" content="text/html; charset=UTF-8">
    <script src="/singular-static/resources/comum/plugins/jquery.min.js" type="text/javascript"></script>
    <script src="/singular-static/resources/comum/scripts/singular-resource-handler.js" type="text/javascript"></script>
    <style type="text/css">
        .login .logo img.logo-sso {
            height: auto;
        }
    </style>
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
            //            .setFavicon("/resources/singular/img/favicon.png")
            .apply();
</script>

<div class="menu-toggler sidebar-toggler">
</div>

<div class="logo">
    <a>
        <img alt="logo" class="logo-default logo-sso" id="brandLogo3"
             src="/singular-static/resources/singular/img/logo_singular.png">
    </a>
</div>

<div class="content">
    <form action="<@spring.url '/login'/>" method="POST" class="login-form">
        <h3 class="form-title">Login</h3>

    <#if RequestParameters.error??>
        <div class="alert alert-danger">
            <button class="close" data-close="alert"></button>
            <span>Preencha o usuário e a senha.</span>
        </div>
    </#if>
        <div class="form-group">

            <label class="control-label visible-ie8 visible-ie9">Usuário</label>

            <input name="username"
                   placeholder="Usuário"
                   class="form-control form-control-solid placeholder-no-fix"
                   id="username"/>
        </div>
        <div class="form-group">
            <label class="control-label visible-ie8 visible-ie9">Senha</label>
            <input type="password"
                   class="form-control form-control-solid placeholder-no-fix"
                   name="password"
                   autocomplete="on"
                   placeholder="Senha"
                   id="password" tabindex="2"/>
        </div>
        <div class="form-actions">
            <button id="btnsubss" type="submit" class="btn uppercase">Login</button>
        </div>

    </form>

</div>
<div class="copyright">
    2017 ©
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