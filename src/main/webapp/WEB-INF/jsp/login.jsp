<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="localization/localization.jsp" %>

<html>
<head>
    <title><fmt:message key="login.page.title"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/login-styles.css"/>">
</head>
<body>
<%@ include file="localization/language-switcher.jsp" %>
<div class="login-container">

    <h2><fmt:message key="login.page"/></h2>
    <form action="${pageContext.request.contextPath}/login" method="post">

        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />

        <div class="form-group">
            <label for="username"><fmt:message key="email"/>:</label>
            <input type="email" name="username" id="username" value='' required>
        </div>
        <div class="form-group">
            <label for="password"><fmt:message key="password"/>:</label>
            <input type="password" name="password" id="password" required>
        </div>
        <button type="submit"><fmt:message key="login.button"/></button>
        <a href="${pageContext.request.contextPath}/registration">
            <button type="button" class="register-btn"><fmt:message key="register"/></button>
        </a>
    </form>

    <c:if test="${not empty error}">
        <div class="error">
            <span>${error}</span>
            <br/>
        </div>
    </c:if>
</div>
</body>
</html>
