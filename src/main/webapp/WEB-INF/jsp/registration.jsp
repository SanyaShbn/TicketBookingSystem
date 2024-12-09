<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title><fmt:message key="registration.title"/></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/registration-styles.css"/>">
</head>
<body>
<div class="registration-container">
    <h2><fmt:message key="registration.title"/></h2>
    <form action="/registration" method="post">
        <div class="form-group">
            <label for="email"><fmt:message key="email"/>:</label>
            <input type="email" name="email" id="email" required>
        </div>
        <div class="form-group">
            <label for="password"><fmt:message key="password"/>:</label>
            <input type="password" name="password" id="password" required>
        </div>
        <div class="form-group">
            <label for="confirm-password"><fmt:message key="confirm-password"/>:</label>
            <input type="password" name="confirm-password" id="confirm-password" required>
        </div>
        <input type="submit" value="<fmt:message key='register'/>">
        <a href="${pageContext.request.contextPath}/login">
            <button type="button" class="back-to-login-btn"><fmt:message key="back.to.login.button"/></button>
        </a>
    </form>

    <c:if test="${not empty requestScope.errors}">
        <div class="error">
            <c:forEach var="error" items="${requestScope.errors}">
                <span>${error.message}</span>
                <br/>
            </c:forEach>
        </div>
    </c:if>

</div>
</body>
</html>
