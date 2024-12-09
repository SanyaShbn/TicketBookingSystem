<%--@elvariable id="arena" type="com.sun.org.apache.xml.internal.security.signature.Manifest"--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<fmt:setBundle basename="messages" />

<html>
<head>
    <title><fmt:message key="create.arena.title" /></title>
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>">
</head>
<body>
<div class="form-container">
    <h1><fmt:message key="create.arena.title" /></h1>
    <form action="${pageContext.request.contextPath}/admin/create-arena" method="post">
        <label for="name"><fmt:message key="arena.name" />:</label>
        <input type="text" id="name" name="name" required>
        <label for="city"><fmt:message key="arena.city" />:</label>
        <input type="text" id="city" name="city" required>
        <label for="capacity"><fmt:message key="arena.capacity" />:</label>
        <input type="text" id="capacity" name="capacity" required>
        <div class="button-group">
            <button type="button" onclick="location.href='${pageContext.request.contextPath}/admin/arenas';">
                <fmt:message key="button.back" />
            </button>
            <button type="submit"><fmt:message key="button.save" /></button>
        </div>
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