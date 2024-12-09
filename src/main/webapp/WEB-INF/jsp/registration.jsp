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
        <label for="email"><fmt:message key="email"/>:
            <input type="email" name="email" id="email" required>
        </label>
        <label for="pwd"><fmt:message key="password"/>:
            <input type="password" name="pwd" id="pwd" required>
        </label>
        <input type="submit" value="<fmt:message key='register'/>">
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
