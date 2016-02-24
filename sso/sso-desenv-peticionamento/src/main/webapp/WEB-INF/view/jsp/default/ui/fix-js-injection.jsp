<%--suppress HtmlUnknownTarget,JspAbsolutePathInspection,ELValidationInJSP --%>
<%@ page session="true" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<script type="text/javascript">
	<% String url = request.getScheme() + "://" +
	   request.getServerName() + 
	   ("http".equals(request.getScheme()) && request.getServerPort() == 80 || 
	    "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) +
	   request.getContextPath(); %>
	if (location.href.indexOf('<%= url %>') != 0){
	    location.href = '<%= url %>?service=<%= request.getParameter("service") %>';
	}
</script>