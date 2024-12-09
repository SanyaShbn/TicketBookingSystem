<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title><fmt:message key="login.page"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/login-styles.css"/>">
</head>
<body>
<div class="login-container">
    <h2><fmt:message key="login.page"/></h2>
    <form action="${pageContext.request.contextPath}/login" method="post">
        <label for="email"><fmt:message key="email"/>:
            <input type="email" name="email" id="email" value="${param.email}" required>
        </label>
        <label for="password"><fmt:message key="password"/>:
            <input type="password" name="password" id="password" required>
        </label>
        <button type="submit"><fmt:message key="login.button"/></button>
        <a href="${pageContext.request.contextPath}/registration">
            <button type="button" class="register-btn"><fmt:message key="register"/></button>
        </a>
    </form>
</div>
</body>
</html>
